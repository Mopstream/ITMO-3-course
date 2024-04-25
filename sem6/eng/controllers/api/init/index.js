const sign = require("../../../module/sign");

module.exports = {
  method: "GET",
  config: {
    rateLimit: {
      max: 10,
      timeWindow: 1000
    }
  },
  async execute(fastify, request, reply) {
    try {
      if (request.sign.vk_user_id == undefined) {
        reply
          .code(403)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send();
      }

      const db = fastify.mongo.db('bingo')
      const users = db.collection('users');
      const bingos = db.collection('bingos');
      users.findOne({
        id: +request.sign.vk_user_id
      }).then((u) => {
        if (u == null) {
          users.insertOne({
            id: +request.sign.vk_user_id
          }).then(() => {
            reply
              .code(201)
              .header('Content-Type', 'application/json; charset=utf-8')
              .send({
                bingos: []
              });
          })
        } else {
          bingos.find({
            creator: +request.sign.vk_user_id,
            status: 0,
            moderation: { $not: { $lt: -1 } },
          }).sort({ created: -1 }).toArray(function (err, b) {
            if (b.length === 0) {
              reply
                .code(200)
                .header('Content-Type', 'application/json; charset=utf-8')
                .send({
                  user: u,
                  bingos: []
                })
            }

            b = b.map((res) => {
              res = { ...res, isLiked: res.likes && res.likes.includes(+request.sign.vk_user_id) }
              res.likes = res.likes.length
              return res
            })


            reply
              .code(200)
              .header('Content-Type', 'application/json; charset=utf-8')
              .send({
                user: u,
                bingos: b
              })
          })
        }
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