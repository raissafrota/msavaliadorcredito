# msavaliadorcredito
Microsserviço independente que faz parte do projeto "cursoms-cartao-credito-cliente".

JAVA 11

A parte descomentada no Dockerfile é o código manual para se criar a imagem do Docker. 
A parte que está comentada no Dockerfile é o código automatizado para se criar a imagem do Docker. 

Para usar o Dockerfile do jeito manual, usar os comandos:

1) ./mvnw clean package -DskipTests => Para gerar o .war
2) Depois usa o docker build --tag nome_imagem . => Para gerar a imagem no Docker.
3) Depois usa o docker run --name nome_container nome_imagem = Para gerar o container no Docker.

Para usar o Dockerfile do jeito automático, usar os comandos:

1) Depois usa o docker build --tag nome_imagem . => Para gerar a imagem no Docker.
2) Depois usa o docker run --name nome_container nome_imagem = Para gerar o container no Docker.

Obs: Caso exista variáveis de ambiente utilizadas no Dockerfile ou no application.yml, é pra passar elas também nos comandos docker.
Ex: docker run --name cursoms-avaliadorcredito --network cursoms-network -e RABBITMQ_SERVER=cursoms-rabbitmq -e EUREKA_SERVER=cursoms-eureka -d cursoms-avaliadorcredito
