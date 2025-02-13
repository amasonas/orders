const { Kafka } = require('kafkajs');

// Configurações do Kafka
const kafka = new Kafka({
  clientId: 'order-producer',
  brokers: ['localhost:9092'], // Kafka rodando localmente
});

const producer = kafka.producer();

// Lista de consumerIds e vendorIds (dados fixos, mas podem ser alterados se necessário)
const consumerIds = Array.from({ length: 10 }, (_, i) => `01234${i + 1}`);
const vendorIds = Array.from({ length: 10 }, (_, i) => `01234${i + 1}`);

// Lista de produtos mockados
const products = [
  { productSku: '12345', unitPrice: 2.0 },
  { productSku: '12346', unitPrice: 3.5 },
  { productSku: '12347', unitPrice: 1.5 },
  { productSku: '12348', unitPrice: 4.0 },
  { productSku: '12349', unitPrice: 2.8 },
  { productSku: '12350', unitPrice: 5.0 },
  { productSku: '12351', unitPrice: 3.0 },
  { productSku: '12352', unitPrice: 2.3 },
  { productSku: '12353', unitPrice: 1.8 },
  { productSku: '12354', unitPrice: 2.2 },
];

// Função para gerar um número aleatório entre `min` e `max`
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// Função para criar um pedido mockado
function createOrder() {
  const orderItems = products.map(product => ({
    productSku: product.productSku,
    quantity: getRandomInt(1, 5), // Quantidade aleatória entre 1 e 5
    unitPrice: product.unitPrice,
  }));

  const order = {
    customerId: consumerIds[getRandomInt(0, consumerIds.length - 1)], // Seleciona um consumerId aleatório
    vendorId: vendorIds[getRandomInt(0, vendorIds.length - 1)],     // Seleciona um vendorId aleatório
    orderItems,
  };

  return order;
}

// Função para enviar as mensagens para o Kafka
async function sendOrder() {
  const order = createOrder();

  await producer.send({
    topic: 'order-entries',
    messages: [
      {
        value: JSON.stringify(order), // Payload que será enviado ao Kafka
      },
    ],
  });

}

// Função principal para executar o mock de pedidos
async function main(durationInSeconds, requestsPerSecond) {
  const totalRequests = durationInSeconds * requestsPerSecond;
  const interval = 1000 / requestsPerSecond;

  await producer.connect();

  let requestCount = 0;

  const intervalId = setInterval(async () => {
    if (requestCount < totalRequests) {
      await sendOrder();
      requestCount++;
    } else {
      clearInterval(intervalId);
      await producer.disconnect();
      console.log(`Enviado ${totalRequests} pedidos.`);
    }
  }, interval);
}

// Lê os argumentos passados na linha de comando
const [,, duration, rate] = process.argv;

if (!duration || !rate) {
  console.log('Por favor, forneça a duração e a taxa de requisições. Exemplo: node mockOrders.js 20 4');
  process.exit(1);
}

main(parseInt(duration), parseInt(rate));
