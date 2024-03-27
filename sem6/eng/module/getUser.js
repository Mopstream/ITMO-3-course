const fetch = require('node-fetch');

let cache = {};
let user = {
  name: "test test",
  ava: "response?.response[0]?.photo_50"
};

module.exports = async (_id) => {
  if (_id in cache) {
    return cache[_id]
  } else {
   let resp = await fetch(`https://api.vk.com/method/users.get?access_token=${process.env.TOKEN}&v=5.140&fields=photo_100&user_id=${_id}`)
   let response = await resp.json()
        user = {
          name: response?.response[0]?.first_name + " " + response?.response[0]?.last_name,
          ava: response?.response[0]?.photo_100
        }
     cache[_id] = user
        return user
    
  }
}