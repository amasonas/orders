# orders

Microserviço para processamento de pedidos.



Este micro serviço foi desenvolvido em Java, na sua versão 21 e com a biblioteca Spring Boot, em sua versão 3.4.2

O fluxo de trabalho começa a partir de um tópico kafka `order-entries` onde ficam armazenados os novos registros e a partir desses registros, a aplicação faz seu processamento, armazenamento e disponibiliza as informações em outro tópico kafka `order-completed`  para consumo do produtoB.

### Primeiros passos

Antes de rodar a solução localmente, é necessário ter no ambiente uma instância do Kafka e uma instância do PostgresDB, caso não tenha, na raiz projeto está disponibilizado um arquivo docker-compose.yml com as imagens das respectivas ferramentas, para subir as imagens, basta executar o seguinte comando na raiz do projeto:
`docker-compose up -d`

O arquivo docker-compose já esta com as credenciais de banco de dados e tópicos kafka configurados.

Após o ambiente estar normalizado, o serviço pode ser executado. Para testar a integração, disponibilizei um script em Javascript para simular as postagens no tópico de entrada.

Este script está disponível no diretório `orders-factory` basta acessar o diretório e executar o seguinte comando antes:

`npm install`

Após  instalação das dependências, basta executar o seguinte script

`node mockOrders.js 10 5`

Onde 10 significa o tempo que durará o envio (em segundos)  e 5 é o número de requisições por segundo que será enviado.