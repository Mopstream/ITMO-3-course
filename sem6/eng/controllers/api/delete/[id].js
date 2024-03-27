const sign = require("../../../module/sign");
const ObjectId = require('mongodb').ObjectID;

module.exports = {
  method: "GET",
  config: {
    rateLimit: {
      max: 1,
      timeWindow: 1000
    }
  },
  async execute(fastify, request, reply) {


    try {
      let id = request.params?.id;
      if (sign(request.headers?.xvk) == undefined) {
        reply
          .code(403)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send();
        return
      }

      const db = fastify.mongo.db('bingo')
      const bingos = db.collection('bingos')

      bingos.updateOne(
        {
          _id: ObjectId(id),
          creator: +request.sign.vk_user_id,
          status: 0
        },
        { $set: { "status": 1 } })
        .then((result) => {
          if (result.result.n !== 1) {
            reply
              .code(404)
              .header('Content-Type', 'application/json; charset=utf-8')
              .send("Not Found");
          }
          reply
            .code(200)
            .header('Content-Type', 'application/json; charset=utf-8')
            .send();
        })
    }
    catch (error) {
      reply
        .code(418)
        .header('Content-Type', 'application/json; charset=utf-8')
        .send(error);
    }
  }
}