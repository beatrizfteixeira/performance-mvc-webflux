#!/bin/bash

# Define os parâmetros do experimento
FRAMEWORKS=("http://localhost:8080/mvc" "http://localhost:8082/webflux")
VUS_LEVELS=(100 800)
ENDPOINTS=("/small-payload/fast-io" "/small-payload/slow-io" "/large-payload/fast-io" "/large-payload/slow-io")
REPLICAS=30

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
          script.js
          
        sleep 5 # Pausa de 5s entre as execuções para o sistema se recuperar
      done
    done
  done
done

echo "--- EXPERIMENTO CONCLUÍDO ---"