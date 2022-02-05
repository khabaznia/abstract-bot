# abstract-telegram-bot

This is ready-to-use bot abstraction that based on:
- Groovy
- Spring Boot
- Telegram API based on webhooks

### Release 1.0.0
Check example here -> https://t.me/example_abstract_bot

---
## Quick start:

### Fork

### Register your bot. Set webhook

### Configure
```
BOT_TOKEN
CHAT_ADMIN
DATABASE_URL
```
### Run

#### Locally

### Heroku

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

Set configs
```
heroku git:remote -a 
heroku config:set BOT_NAME= BOT_TOKEN= CHAT_ADMIN= CHAT_LOGGING= LOGGING_LEVEL= 
```

### Test features with example flows


#### Useful links

- [Heroku](https://dashboard.heroku.com/apps/anyway-bot)
- [Telegram API](https://core.telegram.org/bots/api)

---
# HOW TO USE:

## Map user input (commands).
The app based on webhook and use text input and chat id to understand what resource user wants to access.
Thus, all application resources are marked with the following corresponding annotations to ensure proper mapping:
- [`@BotController`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotController.groovy) - ***Mandatory***, intended to mark class as controller that aggregates specific command mappings. 
- [`@BotRequest`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotRequest.groovy) - ***Optional***, intended to mark method in bot controller with specific command mapping
- [`@Localized`](/src/main/groovy/com/khabaznia/bot/core/annotation/Localized.groovy) - ***Optional***, intended to enable localization of specific command mapping (used in reply keyboards). See localization[^localization]
- [`@Secured`](/src/main/groovy/com/khabaznia/bot/core/annotation/Secured.groovy) - ***Optional***, intended to restrict access to command mapping method to specific user roles.  _Default - ALL_
- [`@Action`](/src/main/groovy/com/khabaznia/bot/core/annotation/Action.groovy) - ***Optional***, intended to specify SendChatAction that should be sent to user, while request is processing. _Default - Typing_

> NOTE!
> You must extend your controller from [`AbstractBotController`](/src/main/groovy/com/khabaznia/bot/controller/AbstractBotController.groovy) to enable pre- and post- processing of update, and get access to specific object[^apiObjects] like sendMessage, editMessage, keyboard etc. 

Example:
```groovy
@BotController() // Mandatory annotation in order mark controller
class ExampleController extends AbstractBotController {

    @BotRequest(path = '/simpleCommand') // simple mapping for user input - /simpleCommand
    simpleCommand() {
        // ...
    }

    @Secured(roles = [Role.ADMIN])
    @BotRequest(path = '/simpleAdminCommand') // /simpleAdminCommand mapping that will be permited only for ADMIN user
    simpleCommandOnlyForAdmin() {
        // ...
    }

    @Action(actionType = ActionType.UPLOADPHOTO) // send specific action type while processing the request. Default ActionType.TYPING
    @BotRequest(path = '/sendMePhoto')
    willRespondWithPhoto() {
        // ...
    }

    @Localized
    @BotRequest(path = 'path.some') // localized mapping that will be invoked for any of this user input -  Some path  OR  Какой-то путь  OR   Якийсь шлях
    someLocalizedPath() {
        // ...
    }
}
```

message_en.properties: 
`path.some=Some path`
message_ru.properties:
`path.some=Какой-то путь`
message_uk.properties:
`path.some=Якийсь шлях`

You can use it all together
```groovy
    @Action(actionType = ActionType.RECORDVOICE)
    @Secured(roles = [Role.USER])
    @Localized
    @BotRequest(path = 'send.video.note')
    sendVideoNote() {
        // ...
    }
```

### Previous path

You can create you own flows using next feautures:
- in [`@BotRequest#after`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotRequest.groovy) you can specify previous action
- you can forward processing of same update to another command mapping by returning `String` from command mapping method with name of command.
- inject parameters from button to target command mapping method arguments

```groovy
    @BotRequest(path = '/firstAction') // first action
    someFirstAction() {
        sendMessage.text('Tap to go to next action') // send user simple message with one button
                .keyboard(inlineKeyboard.button("Got to next action", '/nextAction',
                        [category: 'science'])) // parameter in button
    }
    
    @BotRequest(path = '/nextAction', after = "/firstAction") // this mapping will be invoked ONLY after /firstAction
    // Method returns String and this is trigger to forward to command mapping that will be returned
    String simpleCommandAfterPath(String category) { // The 'category' parameter will be injected from button param ('science' in this example)
        sendMessage.text("You-ve got the category -> $category")
        '/afterNext' // forward to another command
    }
    
    @BotRequest(path = '/afterNext') // This method is regular command, but also will be invoked from previous action 
    afterNextAction() {
        sendMessage.text('That\'s it')
    }
```

### Default mappings and flows

If there are no specific command mapping present in application, some default mappings will be used:

- /any_string - this command is used as empty path in [`@BotRequest`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotRequest.groovy)
- /default - default command mapping if no matching command was found
- /process_image - default command if user send image. Redirects this file to admin chat.
- /process_audio - default command if user send audio. Redirects this file to admin chat.
- /process_video - default command if user send video. Redirects this file to admin chat.

**Also, there some basic flows that are already implemented:**

- /start command that provides ROLE-specific keyboard.
- Changing language flow

![changleLang](demo/change_lang_flow.gif)

- Switch configs flow for ADMIN only. See details in configuration[^configuration].

![changleLang](demo/switch_configs.gif)

### Roles
Eventually app based on two roles: **ADMIN** and **USER**. 
All controllers mapping based on these roles. 

For adding custom role you need:
- add this role to [`UserRole`](/src/main/groovy/com/khabaznia/bot/enums/UserRole.groovy) enum
- extend logic in [`UserService`](/src/main/groovy/com/khabaznia/bot/service/UserService.groovy) to specify how this role of user should be assigned
- extend [`Role`](/src/main/groovy/com/khabaznia/bot/enums/Role.groovy) enum with corresponding role (used in @Secured annotation restricting access to resource in bot controller)

[^apiObjects]:
### Support api methods.

Main methods. How to create. Where to create

[^localization]:
### Builders. Localization. Text methods on String

### Keyboards. Buttons

### MessageTypes

### Logging

[^configuration]:
### Configs and features 

Duplicate messages

Deleting old keyboards

Integration stub

Sending requests in queue

Cron job

Restricted mode

---
## Post MVP:
- [SetCommands](https://github.com/khabaznia/abstract-bot/issues/1) 
- [Payments](https://github.com/khabaznia/abstract-bot/issues/2)
- [Integration tests](https://github.com/khabaznia/abstract-bot/issues/4)
- [Wiremock for local testing of integrations :tada:](https://github.com/khabaznia/abstract-bot/issues/3)