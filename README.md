# abstract-bot
### Clone
```
git remote add upstream https://github.com/khabaznia/abstract-bot.git
git pull upstream master
git push origin master
```

### Run locally
```
gradle clean build bootRun
```

### Things to do before run
1. Create bot using BotFather
2. Run ngrok
3. Set webhook
```
curl -F "url=https://%ngrokHost%.ngrok.io/$BOT_TOKEN" https://api.telegram.org/bot$BOT_TOKEN/setWebhook
```
4. Set configs 
```
BOT_NAME
BOT_TOKEN
CHAT_ADMIN
CHAT_LOGGING
LOGGING_LEVEL
DEFAULT_PORT
DATABASE_URL
```
### Deploy

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Set configs
```
heroku git:remote -a 
heroku config:set BOT_NAME= BOT_TOKEN= CHAT_ADMIN= CHAT_LOGGING= LOGGING_LEVEL= 
```


### Usefull links

- [Heroku](https://dashboard.heroku.com/apps/anyway-bot)
- [Telegram API](https://core.telegram.org/bots/api)

