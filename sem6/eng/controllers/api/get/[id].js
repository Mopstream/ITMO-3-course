const sign = require("../../../module/sign");
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectID;

//get bingo by mongoid

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
      let id = request.params?.id;
      // console.log(id);
      if (sign(request.headers?.xvk) == undefined) {
        reply
          .code(403)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send();
        return
      }

      const db = fastify.mongo.db('bingo')
      const bingos = db.collection('bingos')

      bingos.findOne({
        _id: ObjectId(id),
        status: 0,
        moderation: { $not: { $lt: -1 } },
        $or: [
          { privacy: { $lte: 1 } },
          { creator: +request.sign.vk_user_id }
        ]
      }, function (err, result) {
        if (result == undefined) {
          reply
            .code(404)
            .header('Content-Type', 'application/json; charset=utf-8')
            .send('Not found')
        } else {
          result.isLiked = result?.likes?.includes(+request.sign.vk_user_id)
          result.likes = result?.likes?.length
          reply
            .code(200)
            .header('Content-Type', 'application/json; charset=utf-8')
            .send(result)
        }
      }
      )

    }
    catch (error) {
      reply
        .code(418)
        .header('Content-Type', 'application/json; charset=utf-8')
        .send(error);
    }
  }
}