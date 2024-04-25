const sign = require("../../../module/sign");
const MongoClient = require('mongodb').MongoClient;
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

function pingStatsVK(request) {

  let vk_url = "https://prod-app7766223-5af7e06e83c1.pages-ac.vk-apps.com/index.html?" + request.headers.xvk;


  const events = [
    {
      user_id: request.sign.vk_user_id,
      event: 'vkup_event_action',
      mini_app_id: request.sign.vk_app_id,
      type: 'type_action',
      type_action: {
        type: 'type_mini_app_custom_event_item',
      },
      url: vk_url,
      vk_platform: request.sign.vk_platform,
      screen: 'none',
      json: JSON.stringify({
        event_id: 'track_done'
      }),
    }
  ];

  const url = new URL('https://api.vk.com/method/statEvents.addMiniAppsCustom');
  url.searchParams.append('access_token', process.env.SERVICE_KEY);
  url.searchParams.append('v', '5.131');
  url.searchParams.append('events', JSON.stringify(events));

  // fetch(url.href).catch(() => null);
  fetch(url.href)
    .then(response => response.text())
    .then(text => JSON.parse(text))
    .then(json => console.log(json));
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
    // try {



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
    const users = db.collection('users');

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
        // console.log(result);
        pingStatsVK(request);
        reply
          .code(201)
          .header('Content-Type', 'application/json; charset=utf-8')
          .send(result?.insertedId);
      })





    // }
    // catch (error) {
    //   reply
    //     .code(418)
    //     .header('Content-Type', 'application/json; charset=utf-8')
    //     .send(error);
    // }
  }
}
