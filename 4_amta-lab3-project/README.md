# eECTS

This project is for a Discord Bot integrated betting platform for creating wagers using an equivalent to ECTS study credits.


## Used technologies
The following technologies make up the main building blocks for this project.
- Quarkus for the application development
- Discord4J for the interactions with the Discord API
- JMS / Quarkus Messaging for maintaining the current bets updated in the Discord interface
- PostgreSQL for the database persistence



## Instructions to run locally

To set up and run the application yourself, follow these steps:

### 0. Prerequisites
To launch the service, you need the following dependencies:
* Java 21
* Maven
* Container engine with a Docker CLI (Podman / Docker Engine)

If you have [nix](https://nixos.org/) installed on your system, you can create a shell with all required dependencies
with the following command:
```shell
nix develop
```

### 1. Discord Bot

For your own installation to work you will need to be able to use and manage a Discord bot.

To do this you will need to create a Bot in the Discord Developer Portal, you can follow [this tutorial](https://docs.discord4j.com/discord-application-tutorial). The second part of the tutorial explains how to invite the bot to the Discord server so it can access the necessary channels for users to interact with it.

### 2. Dependencies

Once your bot is linked to the Discord server you want to use, you need to have some environment variables set so the Bot's code can find the server and some other useful settings. Base your `.env` document on the following template and store it in the root of the project structure.

```
app.bot-token=<Bot authentication token>
app.guild-id=<Server ID>
app.betting-board-channel-id=<Channel ID>
app.external-url=<url for web interface, e.g. http://localhost:8080/>
```

- `Bot authentication token`: found in the "Bot" settings of the developer portal
- `Server ID`: right click the server title, click `Copy Server ID`
- `Channel ID`: right click the channel name, click `Copy Channel ID`

All other dependencies are implemented in the `pom.xml` file managed by maven.



## Bot usage

All interactions to do with bets (create, close, ...) are done via the Bot directly in the chat.

### Commands

The following commands are available to be used by the users, for each bet you can also find the variables to be supplied by the user:

- `/create-bet`: creates a new bet
    - `name`: name of the bet to be created
    - `odds_for`: multiplier for a winning bet if the outcome is in favor of the bet
    - `odds_against`: multiplier for a winning bet if the outcome is against the bet
    - `closing_date`: limit for accepting new bets (1: hour, 2: day, 3: week)
    
    The newly created bet will appear in the channel you refer to in the `.env` file.

    ![Bet message](/docs/images/Bet.png)
    - `Bet - For`: opens the amount input for a bet in favor of the described outcome
    - `Bet - Against`: opens the amount input for a bet against the described outcome
    - The last button opens the web interface for the current bet

    ![Amount input popup](/docs/images/BetAmount.png)

- `/credits`: shows the user their current credits balance
  The user will receive a message only they can see containing their current balance.

  ![Balance popup](/docs/images/Credits.png)

- `/leaderboard`: shows the current leaderboard sorted by the user with the most credits
  The user will receive a message only they can see containing the current user rankings.

  ![Leaderboard message](/docs/images/Leaderboard.png)

- `/close-bet`: lists the bets the user can close (they have to have ended and be owned by the calling user)
  The user receives a message only they can see with a list of bets they can close.
  (They will not see anything if none of the bets they created are past their closing time)

  ![Empty closing message](/docs/images/CloseEmpty.png)

  ![Closing message](/docs/images/CloseSelection.png)

  Once the desired bet is chosen, they define what the outcome was (For or Against).

  ![Outcome popup](/docs/images/OutcomeSelection.png)

  This triggers the payouts for the winning users.

  ![Payout message](/docs/images/Payout.png)



## Web interface

The web interface is accessible at the url you set in the `.env` file. This allows users to easily have an overview of the open bets, the archived bets and the leaderboard.

### Bets

`<chosen url>/bets`

![Open bets](/docs/images/OpenBets.png)

#### Specific bet

`<chosen url>/bets/<bet id>`

![Bet details](/docs/images/BetDetails.png)

### Archive

`<chosen url>/bets/archive`

![Bets archive](/docs/images/Archive.png)

### Leaderboard

`<chosen url>/leaderboard`

![Leaderboard](/docs/images/WebLeaderboard.png)