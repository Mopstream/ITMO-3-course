const sign = require("../../../module/sign");
const MongoClient = require('mongodb').MongoClient;
const ObjectId = require('mongodb').ObjectID;

//create bingo

function isValidBody(obj) {
  // console.log(obj);
  if (obj.like == undefined) {
    return false
  }
  return true
}



module.exports = {
  method: "POST",
  config: {
    rateLimit: {
      max: 1,
      timeWindow: 1000
    }
  },
  async execute(fastify, request, reply) {
    try {
      let id = request.params?.id;
      // console.log(id);


      if (request.sign.vk_user_id == undefined) {
        reply
          .code(403)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send();
        return
      }


      let obj = await JSON.parse(request.body);

      if (!isValidBody(obj)) {
        reply
          .code(400)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send([obj]);
        return
      }

      const db = fastify.mongo.db('bingo')
      const bingos = db.collection('bingos');
      const users = db.collection('users');
      bingos.updateOne(
        {
          "_id": ObjectId(id),
          $or: [{ privacy: { $lte: 1 }}, {creator: +request.sign.vk_user_id }],
          status: 0
        },
        obj.like == 1 ?
          {
            $addToSet: { "likes": +request.sign.vk_user_id }
          } : {
            $pull: { "likes": +request.sign.vk_user_id }
          })
        .then((result) => {
          // console.log(result);
          if (result.result.n !== 1) {
            reply
              .code(404)
              .header('Content-Type', 'application/json; charset=utf-8')
              .send("Not Found");
          }
          if (result.result.nModified !== 1) {
            reply
              .code(400)
              .header('Content-Type', 'application/json; charset=utf-8')
              .send("Non modified");
          }
          reply
            .code(200)
            .header('Content-Type', 'application/json; charset=utf-8')
            .send(result);
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