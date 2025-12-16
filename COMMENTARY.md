# Introduction
Im a bit late to the party but this is my first attempt at earnestly engaging with AI. Im going to be building a stock taking app that can handle storing information on products for a theoretical retail company. I plan on relying on the model as much as possible as that is what i have been told to do. I will attempt to create a commit for each and every prompt i write, i will then paste the prompt here along with my running commentary on how i believe this project to be going.

I am using cursor as my IDE and using the in built prompt functionality to interface with claude. I am not using any cli tools as while i am trying to enter this with an open mind i unfortunately do not trust an LLM to be running shell commands on my machine so this separation is necessary for me at the moment.

# Dev Dairy

## Project setup
To start out this project i am creating an initial commit containing this file along with a `./claude/.settings` file and a `.cursorignore` file that excludes this file from context (does not work and i cannot work out how to exclude a file from context). Unfortunately information on how to best use agents to generate code seems to be sparse and conflicting, this somewhat raises the question of why people feel i need to learn this technology if no one can seem to provide concrete education on how to properly use it, but i digress. One prevailing thought though seems to be that when starting a new project its best to begin with asking claude to help you develop a plan before starting so thats what i intend to do now.

## Prompt 1
>Create a plan for an app to maintain a product catalog. this app should have a database of products allowing users to add new products and edit details of existing products. it should have a backend written in kotlin and a frontend written in typescript react. it should also use postgres for a database.

### Before prompt:
- I am executing this in cursors plan mode.
- The backend code and database selection are purely as those are the things i have the most experience

### During plan:
- The very first thing it did was read this file. i fiddled with this a lot to try and get it to not read this file and i cannot work it out. it may be a problem with me as im using cursor but the docs on this really do not seem all that great and i decided to live with it.
- It asked me some somewhat sensible questions about what fields the products should have, i went for the most basic options as this is what i would do when building an application like this.
- It asked if i wanted to add authentication to which i said yes too.
- It asked what framework it should use, i said ktor as its what i have the most experience with.
- After all this it created a relatively sensible plan for the app, it then gave me an opportunity to edit this plan if i felt like i needed to which is nice.
- Most of its choices where ok except for the fact that it seems to want to do password storage and authentication entirely by itself. This to me feels incredibly short sighted as if i were building an app to go into a production scenario i would definitely want to offload authentication to a third party, it would be insane to store all your account details yourself. However i didn't really tell it what to do for auth and i dont really have a plan for it anyway so in true vibe coding fashion i decided to kick that can down the road and let it run its plan.

### Results:

#### The good
- It created a decent format for data structure. it separated the request objects from the database structure which is something i find real developers missing on occasion.
- It did an ok job of abstracting out logic into sensible services.
- It seemed to make good use of ktor plugins.
- Most of the frontend looks relatively reasonable. However i will need to look into it more once i can get everything running properly

#### The bad
- The run command it gives in the readme for the backend does not work as it references a gradle wrapper file that it did not include. (it probably shouldn't generate this file and should get gradle to generate it but it also provides no mention of how to do that)
- After i booted up my IDE which did generate the gradle wrapper file the backend failed to compile as it simply did not import some of its required fields to create tables.
- It provided no functionality to support an existing or evolving database. It simply creates the tables it wants on startup and will likely fail if ran twice consecutively. (i will come back and double check this once i am able to get it running).
- It places all the database logic in "services" and much of the "services" logic in routes.
- Stores all frontend types in one filem
- Frontend stores all api integrations in one file.

#### Neutral
- It built this under the impression that products are not shared between users and each user would only maintain a list of products for personal use. This makes sense as the model has likely seen a lot of customer facing examples and not many internal tools. This is kinda highlights one of my main gripes with these tools in that natural language is a bad way to communicate discrete concepts.
- It seemed to enforce a lot of database constraints without asking that could cause issues later on.
- It separated the data structure of a request coming in from the API from one coming internally. This seems a slightly excessive amount of abstraction far too early on but i have seen developers do worse.
- Nitpicking here but it used * imports so its hard to see where its actually getting things from without pulling the code into an IDE.
- All the database operations returned a kotlin "Result" which i have never used before but will return an object that may contain an exception which to me feels gross. but its basic kotlin functionality so whatever
- Frontend seemed to write its own auth provider. TBH im not sure if this is good or bad, but react is not phenomenal.

### Manual changes
- i pulled the backend into IntelliJ so i could scrutinize it a bit better. Which wound up running a gradle init and generating some supporting gradle files.
- i created a `.gitignore` to ignore files i don't want tracked.

## Prompt 2:
For this prompt i copied the many compilation errors from my terminal i into the model

### The good
- It seemed to realize a few of its mistakes, added a missing import, removed a redeclaration of the same object in separate files (problem with * imports).

### The bad
- Made some random changes around defaults and in other places.
- left an unused import.
- Still doesn't compile.

## Prompt 3:
Once again copy pasted the compilation errors from my shell and it once again gave an output that didn't compile. This was due to it refusing to fix one of the errors in the list and it simply hallucinating an import that was wrong and unnecessary. From here i decided i will just need to manually fix these errors.

## Manual Fixes:
Manually fixed the compilation errors. most boiled down to incorrect imports or a file being in the wrong place. the app now runs and seems to work more or less as expected.

## Prompt 4:
>products should not be limited to a single user and should be available to be edited and updated by any user

So it did a reasonable job of this but i think i have hit my first major snag with this. on of the imports i added to fix a compilation error has been removed again which causes the compilation error again.

I gave it the compilation error and it babbled some nonsense about the method being out of scope and simple re-ordered some code which fixed nothing. I guess for me this is an opportunity to play with some of the numerous ways to insert extra context into the model.

## Update 5
Im finding that one commit per prompt doesn't really work, the LLM needs much more hand holding than 1 or 2 prompts per operation so im moving to a an update every commit.

this commit simply forces the LLM to not use wildcard imports and that seems to fix its issues with forgetting or hallucinating imports.

## Update 6
I got it to update the products to contain a stock count. I did this without a plan as i wanted to test how far it could go and it did not do to well.

It failed to update the database with the necessary column, however it did somewhat impressively sort that out once i have it the error message.

I was interested to see how it handled a new field as obviously old data will not contain this entry. What it did was apply a default of 0 in the table definition. This is one of those decisions that im very concerned about AI making, its something that should require thought. In this instance it could be correct however so ill defer some judgement there. The other aspect of this is that i as a developer should be thinking about this and guiding it but this is easily a thing i might miss when developing a new feature and if i dont pay close enough attention then i may not think about it, where as if i built this myself i would forcibly run into it. Also im not 100% sure how much the LLM makes me if i have to think about things to this sort of granularity, which is my main concern with the technology in general if im honest but again i digress.

## Update 7
While creating the last update i noticed an issue. Earlier i hinted that there is no method for evolving data, i never told it to create a method for evolving data so im going to acknowledge that i am likely hitting a limit of this things capabilities now.

As such told it to use flyway for migrations so we dont have this issue.

One oddity here. It asked me to create a directory as part of this change
```
mkdir -p <project base>/backend/src/main/resources/db/migration
```
Really not sure why it asked me to do this. It seems to have no issues creating other directories. Not a big issue but feels a little scuffed.

Another oddity. It split the creation of the users table and the products table into 2 different migration files. Again, not a huge deal but a little scuffed.

Other than that things went relatively smoothly

## Update 8
Tried to add created by and updated by functionality. The migration it wrote first failed because it added a non null column to a table that has existing records. Again this is probably me expecting too much from this thing i will remember to give it a default next time. When i pointed this out it tried to default all records to be created by and updated by the first user in the database. Which is slightly silly but again i didn't give it something to do so ill give it a pass.

It also wrote some misleading comments in this migration:
```
-- Update existing products to use the first user in the system
-- (or create a system/admin user if needed)
UPDATE products
SET created_by = (SELECT id FROM users ORDER BY created_at LIMIT 1),
    updated_by = (SELECT id FROM users ORDER BY created_at LIMIT 1)
WHERE created_by IS NULL;
```
Maybe that second comment is a recommendation for me but it does a poor job of explaining that.

One of the strange things its telling me to do is to dropped the failed migration from the database. Which in my opinion is both uneccicary and a poor decision. That record is there to show a history of the migrations that this thing attempted to apply, we really should not be touching that. I guess it would be fine to do locally but it is also completely uneccicary to do locally so i don't like that recommendation.

## Update 9
While testing the previous change i discoverd that i am getting logged out on refresh. It seems we are pulling the user information out of state on every refresh which is is taking some time and in that time the route things im unauthenticated and sends me back to the login page.

The solution was to return a wait and then block until this is loaded out of state. again i don't know react well enough to comment on this but seems a little iffy.

## Update 10
Adding the previous created by and updated by functionality to the frontend. Had to hold its hand a bit on this one because it kept trying to insert loads of user data into the product response but eventually it spat out a fairly reasonable answer.