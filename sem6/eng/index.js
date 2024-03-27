const MongoClient = require('mongodb').MongoClient;
const sign = require("./module/sign")
require('dotenv').config();

const fastify = require('fastify')({
  logger: {
    level: 'info',
    file: `log.log`
  }
});
fastify.register(require('fastify-formbody'));
fastify.register(require('fastify-cors'), {
  origin: "*",
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
})

// Добавляем плагин

fastify.register(require('fastify-rate-limit'),
  {
    global: false,
    keyGenerator: (request) => {
      let param = sign(request.headers.xvk);
      if (param) {
        request.sign = param;
        return param.vk_user_id;
      } else { return "undefined" }
    },
    errorResponseBuilder: () => { return undefined },
  });

fastify.register(require('fastify-easy-route'));

const start = () => {
  try {
    fastify.listen(process.env.PORT || 80, process.env.IP || '0.0.0.0', (error) => {
      if (error) {
        fastify.log.error(error);
        process.exit(1);
      }
      console.log(`Server listening on http://${process.env.IP || '0.0.0.0'}:${process.env.PORT || 80}`)
    })

    const mongo = new MongoClient(process.env.MONGO_URL, { useNewUrlParser: true, useUnifiedTopology: true });

    mongo.connect(
      (err, client) => {
        if (err) {
          console.log('Connection error: ', err)
          throw err
        }

        fastify.mongo = client
      }
    )


  }
  catch (error) {
    fastify.log.error(error);
  }
}

start()
