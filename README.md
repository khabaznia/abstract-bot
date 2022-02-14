# abstract-telegram-bot

This is ready-to-use bot abstraction that based on:
- Groovy
- Spring Boot
- Telegram API based on webhooks

**[Check an example of current version](https://t.me/example_abstract_bot)**

_Release 1.0.0_

---
## How to start with your bot

- Fork
```shell
git clone git@github.com:khabaznia/abstract-bot.git <new-repo-name>
cd <new-repo-name>
git remote rename origin abstract
git remote add origin git@github.com:<new-repo-remote-link>
git push origin master 
```
- Register your bot with [BotFather](https://t.me/BotFather)
- Set [privacy mode](https://core.telegram.org/bots#privacy-mode) if needed for groups.
- Set configs
```shell
export DATABASE_URL=
export BOT_TOKEN=
export CHAT_ADMIN= 
export LOGGING_LEVEL= 
```
- Deploy
   - On local:
     - install [ngrok](https://ngrok.com/)
     - create DB
     - run 
     
    ```shell
    ngrok http 8083
    gradle clean build bootRun
    ```
   - Create app on heroku:
  
  ```shell
    heroku apps:create <app-name>
    heroku git:remote -a <app-name>
    heroku addons:create heroku-postgresql:hobby-dev -a <app-name>
    heroku ps:type hobby  
    heroku config:set LOGGING_LEVEL='INFO' BOT_TOKEN='<bot-token>' CHAT_ADMIN='<admin-chat>'
    git push heroku master
    ```
- Set webhook
> If your run on local use ngrok https alias as `<host-url>`
```shell
curl -F "url=<host-url>/<bot-token>" https://api.telegram.org/bot<bot-token>/setWebhook
```




---
# HOW TO USE:

## Map user input (commands).
The app based on webhook and use text input and chat id to understand what resource user wants to access.
Thus, all application resources are marked with the following corresponding annotations to ensure proper mapping:
- [`@BotController`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotController.groovy) - ***Mandatory***, intended to mark class as controller that aggregates specific command mappings. 
- [`@BotRequest`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotRequest.groovy) - ***Optional***, intended to mark method in bot controller with specific command mapping
- [`@Localized`](/src/main/groovy/com/khabaznia/bot/core/annotation/Localized.groovy) - ***Optional***, intended to enable localization of specific command mapping (used in reply keyboards).
- [`@Secured`](/src/main/groovy/com/khabaznia/bot/core/annotation/Secured.groovy) - ***Optional***, intended to restrict access to command mapping method to specific user [roles](https://github.com/khabaznia/abstract-bot#roles).  _Default - ALL_
- [`@Action`](/src/main/groovy/com/khabaznia/bot/core/annotation/Action.groovy) - ***Optional***, intended to specify SendChatAction that should be sent to user, while request is processing. _Default - Typing_

> NOTE!
> You must extend your controller from [`AbstractBotController`](/src/main/groovy/com/khabaznia/bot/controller/AbstractBotController.groovy) to enable pre- and post- processing of update, and get access to specific [object](https://github.com/khabaznia/abstract-bot#available-methods) like sendMessage, editMessage, keyboard etc. 

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

- Switch configs flow for ADMIN only. See details in [configuration](https://github.com/khabaznia/abstract-bot#configuration).

![changleLang](demo/switch_configs.gif)

### Roles
Eventually app based on two roles: **ADMIN** and **USER**. 
All controllers mapping based on these roles. 

For adding custom role you need:
- add this role to [`UserRole`](/src/main/groovy/com/khabaznia/bot/enums/UserRole.groovy) enum
- extend logic in [`UserService`](/src/main/groovy/com/khabaznia/bot/service/UserService.groovy) to specify how this role of user should be assigned
- extend [`Role`](/src/main/groovy/com/khabaznia/bot/enums/Role.groovy) enum with corresponding role (used in @Secured annotation restricting access to resource in bot controller)

## Available methods.

The app has implementation of [wrappers](/src/main/groovy/com/khabaznia/bot/meta/request/impl) to main api method:
_SendMessage, EditMessage, DeleteMessage, PinMessage, SendAudio, SendVideo, SendPhoto, SendChatAction._
These objects intended to wrap data that should be sent to user in convenient way. 
Objects are based on Builder pattern, so you can fill its fields easily. 

The recommended way to use it - get needed bean from context, fill its fields, and execute it by publishing [ExecuteMethodsEvent](/src/main/groovy/com/khabaznia/bot/event/ExecuteMethodsEvent.groovy).
AbstractBotController contains methods that enables you to create such wrappers, and it will execute created wrappers after method invocation.

Let's check an example:

```groovy
@BotController
class ExampleController extends AbstractBotController {

  @BotRequest(path = '/command')
  someFirstAction() {
    sendMessage // this invokes method getSendMessage() from AbstractBotController that creates sendMessage object and then will execute it 
        .text('message.text.label')
        .label('mylabel')
        .keyboard(inlineKeyboard.button("Edit this message", '/editMessage'))
    sendAudio // this invokes method getSendAudio() from AbstractBotController that creates sendAudio object and then will execute it 
        .text('some caption to audio')
        .audio(SOME_SAVED_AUDIO_ID)
  }

  @BotRequest(path = '/editMessage')
  editPreviousMessage() {
    editMessage // this invokes method getEditMessage() from AbstractBotController that creates editMessage object and then will execute it 
        .text('This was changed')
        .label('mylabel') // message with label 'myLabel' will be edited with new text and keyboard  
        .keyboard(inlineKeyboard.button("Next?", '/nextAction'))
  }
}
```

> Localization
Field `text` in wrappers is localized, so when request wrapper is converted to Telegram Api Methods, 
the localized property will be retrieved from messages.properties file using locale of current user.
Same for `text` fields in keyboard buttons.

### Keyboards, Buttons
Same to api methods, app contains wrappers for keyboards and buttons.
There are two types of keyboards:
[`InlineKeyboard`](/src/main/groovy/com/khabaznia/bot/meta/keyboard/impl/InlineKeyboard.groovy)
[`ReplyKeyboard`](/src/main/groovy/com/khabaznia/bot/meta/keyboard/impl/ReplyKeyboard.groovy)

Both of them has methods to create buttons. 
Let's proceed with example:

```groovy
  @Localized
    @BotRequest(path = '/example', enableDuplicateRequests = true)
    getReply() {
      sendMessage
        .text('Here is your reply keyboard')
        .replyKeyboard([[MODIFIABLE_INLINE_KEYBOARD, EDITING_MESSAGES, INTEGRATION_TESTS_KEYBOARD],
                        [EXAMPLE.addEmoji(MEDITATE)], 
                        [TO_MAIN.addEmoji(LEFT_ARROW)]])

    }
```
![](demo/reply_keyboard.jpg)

```groovy
  @Localized
  @BotRequest(path = 'path.modifiable.keyboard')
  getFeatures() {
    sendMessage.text('modifiable.inline.keyboard')
      .keyboard(inlineKeyboard
        .button('button.example.simple', AVOCADO, '/query', [(UNLIMITED_CALL): 'true']) 
        .buttonWithBinding('button.example.binding', '/query', [binding: 'Some'])
        .row()
        .oneTimeButton('button.one.time.simple', '/default')
        .oneTimeButton('button.one.time.with.query', BOAT, '/query')
        .oneTimeButton('button.one.time.with.param', '/queryWithParam', [someUniqueId: 'data from button'])
        .row()
        .switchButton('button.example.switch', '/default', true, [someUniqueId: 'some data from button']))
 }

  @BotRequest(path = '/query')
  query() {
    sendMessage.text('<b>query</b> - ok').delete()
  }
  
  @BotRequest(path = '/queryWithParam')
  query(String someUniqueId) {
    sendMessage.text("This param was in button request -> $someUniqueId").delete()
  }
```
![](demo/inline_keyboard.gif)

You can check how keyboards and buttons can be implemented in [ExampleController](/src/main/groovy/com/khabaznia/bot/controller/example/ExampleController.groovy).

### Additional `String` methods

There are some additional methods available on `String` class:
- bold
- italic
- underline
- strikethrough

```groovy
@BotRequest(path = '/checkTexts')
  checkTexts() {
    sendMessage.text('some bold'.bold())
    sendMessage.text('some italic'.italic())
    sendMessage.text('some underline'.underline())
    sendMessage.text('some strikethrough'.strikethrough())

    // for localized values
    sendMessage.text('test.bold'.bold()) 
    sendMessage.text('test.italic'.italic()) 
    sendMessage.text('test.underline'.underline())
    sendMessage.text('test.strikethrough'.strikethrough())
  }
```

![](demo/check_texts.gif)

### MessageTypes
Each api method wrapper (BaseRequest) has [`MessageType`](/src/main/groovy/com/khabaznia/bot/enums/MessageType.groovy) that provides additional pre- and post- processing of the request.

- **SKIP** - Do not save in DB, default `SendMessage`
- **PERSIST** - Just save to DB
- **DELETE** - Send message and deletes it with next request.
- **INLINE_KEYBOARD** - Default for message with inline keyboard. Inline keyboards can be updated. Saved in DB.
- **REPLY_KEYBOARD** - Default for message with reply keyboard. Saved in DB.
- **ONE_TIME_INLINE_KEYBOARD** -  Message with inline keyboard that should be deleted after its any button click.
- **PINNED** - After sending, saved to DB, send additional request to pin the message.
- **EDIT** - Edit existing message. Updates it in db. Default for `EditMessage`.
- **EDIT_AND_DELETE** - Edit existing message. Updates in db. Deleted it with next message.
- **MEDIA** -  For AUDIO, IMAGE, VIDEO messages. Default for `SendPhoto`, `SendVideo`, `SendAudio`.

You can explicitly specify it in builder

```groovy
sendMessage
        .text('This message should be pinned')
        .type(MessageType.PINNED)

sendMessage
        .text('This message will be deleted')
        .type(MessageType.DELETE)

// or another variant for MessageType.DELETE   
sendMessage
        .text('This message will be deleted')
        .delete()
```

## Configuration 

The configuration of app is presented in [application.properties](/src/main/resources/application.properties) and [application.yml](/src/main/resources/application.yml) files

Config properties are saved to DB in order to save state of application while new release deploy. 
You can simply get config value using methods in [`Configurable`](/src/main/groovy/com/khabaznia/bot/trait/Configurable.groovy) trait.

There are specific prefixes for config properties:
- **env.only** - property don't saved to DB
- **switchable.config** - property should be boolean, and can be managed from [switch features flow](https://github.com/khabaznia/abstract-bot#default-mappings-and-flows) by admin user.

Next properties are needed to start application and should be specified as system properties (recommended) or in [application.yml](/src/main/resources/application.yml) file

```yaml
logging:
  level:
 ...
    com.khabaznia.bot: ${LOGGING_LEVEL} // mandatory to specify logging level

env.only:  
 ...
  bot:
    token: ${BOT_TOKEN} // mandatory, token of your bot
    admin.chat.id: ${CHAT_ADMIN} // mandatory, specified admin chat. Use https://t.me/userinfobot to check id of user
```

### Logging to chat

You can set any group as logging chat in order to check what actions are performed in the application, 
by **'/setLogging'** command that is available to admin.


Next type of logging available:
- **DEBUG** - logs every update info to specified `CHAT_LOGGING` if feature is enabled
- **INFO** - logs to message to `CHAT_LOGGING`. Use [`Loggable`](/src/main/groovy/com/khabaznia/bot/trait/Loggable.groovy) trait or publish [`LogEvent`](/src/main/groovy/com/khabaznia/bot/event/LogEvent.groovy) with specified message.
- **WARN** - logs any errors of application to `CHAT_LOGGING`, and duplicates the message to `CHAT_ADMIN` if feature is enabled

Use [`Loggable`](/src/main/groovy/com/khabaznia/bot/trait/Loggable.groovy) trait to log any message.

```properties
switchable.config.debug.logging.enabled=false
switchable.config.duplicate.warn.logging.to.admin=true
```

### Additional features:

#### Duplicate messages

Disables to access to the same resource as a previous action:
```yaml
block.duplicate.requests: true
```

Can be explicitly turned off for enabled feature:
- _for command mappings_ - [`@BotRequest#enableDuplicateRequests`](/src/main/groovy/com/khabaznia/bot/core/annotation/BotRequest.groovy)
- _for buttons_ - using **UNLIMITED_CALL** parameter
```groovy
.button('button.example.simple', AVOCADO, '/query', [(UNLIMITED_CALL): 'true'])
```

#### Deleting old keyboards

Enables to delete old inline keyboards except the one that was sent as response to last update.
Helps to save chat history clean.

```properties
switchable.config.delete.previous.inline.keyboards=true
```

#### Sending requests in queue

As telegram api has [limits](https://core.telegram.org/bots/faq#my-bot-is-hitting-limits-how-do-i-avoid-this) of executing requests, there is a solution to execute requests in queue.

Feature is recommended if most of the messages sent in groups. 
For private chats it can be disabled.

```properties
switchable.config.execute.requests.in.queue=false
```
If 429 error is occurred, not send requests will be executed in queue, even if feature disabled.

Queue can be additionally configured by properties:
```yaml
chat.inactive.minutes: 15
requests.per.second: 33
requests.delay.limit.in.single.chat: 450
```

#### Restricted mode

If you want to restrict access to bot resources, you can specify comma-separated user ids and enable feature by configs: 
```yaml
restricted.mode: false
restricted.mode.users: ${RESTRICTED_USERS}
```

#### Database clean up job
Since messages and keyboards are stored in database, regular cronjob is needed to delete old and orphaned objects in DB.

The job is configurable by following properties:
```yaml
expired.paths.in.days.count: 2
clean.up.database.cron.expression: 0 0 2 * * *
```

## API to send messages via bot

You can send messages via bot using api endpoint `/api/sendMessage/`
```json
{
    "chatId": "65534234", // optional. If empty sends message to admin user
    "text": "Hi from api"
}
```

Swagger documentation is available on `/swagger-ui`

Link to [Swagger of Example bot](https://khabaznia-quest-bot.herokuapp.com/swagger-ui/#)

---
## Post MVP:
- [SetCommands](https://github.com/khabaznia/abstract-bot/issues/1) 
- [Payments](https://github.com/khabaznia/abstract-bot/issues/2)
- [Integration tests](https://github.com/khabaznia/abstract-bot/issues/4)
- [Wiremock for local testing of integrations :tada:](https://github.com/khabaznia/abstract-bot/issues/3)

---
### Useful links
- [Heroku](https://dashboard.heroku.com/apps/anyway-bot)
- [Telegram API](https://core.telegram.org/bots/api)
