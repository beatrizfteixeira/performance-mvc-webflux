import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

// Métricas customizadas para registrar latência por framework
const mvcLatency = new Trend('mvc_req_duration', true);
const webfluxLatency = new Trend('webflux_req_duration', true);

// As opções de execução serão controladas por variáveis de ambiente
export const options = {
  scenarios: {
    load_test: {
      executor: 'constant-vus', // Mantém um número constante de usuários virtuais
      vus: parseInt(__ENV.VUS) || 100, // Número de VUs (Fator A)
      duration: '20s', // Duração otimizada para 30min totais
    },
  },
  thresholds: {
      http_req_failed: ['rate<0.10'], // 10% - permite observar degradação sob estresse
      http_req_duration: ['p(95)<10000'], // 10s - tolerante para cenários de alta carga
  },
};

export default function () {
  // A URL e o endpoint são passados como variáveis de ambiente
  const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/mvc';
  const ENDPOINT = __ENV.ENDPOINT || '/small-payload/fast-io';

  // Executa a requisição GET
  const res = http.get(`${BASE_URL}${ENDPOINT}`);

  // Checa se a requisição foi bem sucedida
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  
  // Adiciona a métrica de latência ao Trend correto
  if(BASE_URL.includes("8080")) { // Assumindo que 8080 é MVC
      mvcLatency.add(res.timings.duration);
  } else if (BASE_URL.includes("8082")) { // Assumindo que 8082 é WebFlux
      webfluxLatency.add(res.timings.duration);
  }

  // Sleep ajustado para Windows (evitar esgotamento de portas)
  sleep(0.2);
}
