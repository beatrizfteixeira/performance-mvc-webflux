#!/bin/bash

# Define os parâmetros do experimento (otimizado para 30min totais)
FRAMEWORKS=("http://localhost:8080/mvc" "http://localhost:8082/webflux")
VUS_LEVELS=(50 200)  # Reduzido para Windows (limitação de portas efêmeras)
# Focar nos endpoints críticos para validar hipótese
ENDPOINTS=("/small-payload/slow-io" "/large-payload/fast-io")
REPLICAS=10  # Reduzido para caber em 30min

# Cria um diretório para os resultados
mkdir -p results

# Loop principal
for framework_url in "${FRAMEWORKS[@]}"; do
  framework_name=$(echo $framework_url | cut -d'/' -f4) # mvc ou webflux
  
  for vus in "${VUS_LEVELS[@]}"; do
    for endpoint in "${ENDPOINTS[@]}"; do
      echo "--- EXECUTANDO: Framework=$framework_name, VUs=$vus, Endpoint=$endpoint ---"
      
      for (( r=1; r<=$REPLICAS; r++ )); do
        echo "=> Réplica #$r de $REPLICAS"
        
        # Define o nome do arquivo de saída
        output_file="results/${framework_name}_vus${vus}_ep${endpoint//\//-}_r${r}.json"
        
        # Executa o k6 e salva o resumo em um arquivo JSON
        k6 run \
          -e BASE_URL="$framework_url" \
          -e VUS="$vus" \
          -e ENDPOINT="$endpoint" \
          --summary-export="$output_file" \
          script-2.js
          
        sleep 3 # Pausa de 3s entre as execuções para o sistema se recuperar
      done
    done
  done
done

echo "--- EXPERIMENTO CONCLUÍDO ---"