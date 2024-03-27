const fetch = require('node-fetch');
const getUser = require('../../../module/getUser');

function isValidBody(obj) {
  if (obj.title == undefined ||
    typeof obj.title != "string" ||
    obj.title.length < 2 ||
    obj.title.length > 20 ||

    obj.size == undefined ||
    obj.size > 5 ||
    obj.size < 3 ||

    obj.text == undefined ||
    obj.text.length != obj.size ** 2) {

    return false
  }

  for (i of obj.text) {
    if (typeof i != "string") {
      return false
    }
    if (i.length == undefined || i.length > 100) {
      return false
    }
  }

  return true
}


module.exports = {
  method: "POST",
  config: {
    rateLimit: {
      max: 1,
      timeWindow: 5000
    }
  },
  async execute(fastify, request, reply) {

    if (request.sign.vk_user_id == undefined) {
      reply
        .code(403)
        .header('Content-Type', 'application/json; charset=utf-8')
        .send();
      return
    }

    let obj = request.body;

    if (!isValidBody(obj)) {
      reply
        .code(400)
        .header('Content-Type', 'application/json; charset=utf-8')
        .send([obj.title,
        obj.size,
        obj.isChecked,
        obj.text]);
      return
    }

    const db = fastify.mongo.db('bingo')
    const bingos = db.collection('bingos');

    let author = await getUser(request.sign.vk_user_id)
    bingos.insertOne({
      creator: +request.sign.vk_user_id,
      cr_name: author.name,
      cr_ava: author.ava,
      privacy: obj.privacy,
      status: 0,
      title: obj.title,
      size: obj.size,
      text: obj.text,
      isChecked: 0,
      likes: [],
      created: Number(new Date())
    })
      .then(result => {
        reply
          .code(201)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send(result?.insertedId);
      })
      .catch(error => {
        reply
          .code(500)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send(error);
      });
  }
}
