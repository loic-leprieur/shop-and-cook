# Shop & Cook : An app for foodies <3

## Concept

It starts with an idea of a **recipe** or a specific **ingredient** that you would like to cook.
The application provide a **REST** populated list of recipes and very useful tools to make him
the perfect cook. 

If the user does need to shop something, then the app will guide him to fetch all 
required ingredients in the *closest shops*, *even when he isn’t looking at his phone*.

Like a cooking assistant, the application will *remind you to get ingredients* 
when you are near a relevant shop. Plus, you can schedule your next cooking session.
Finally, when you are ready to start cooking,
the app will provide you *step by step guidelines* to make the best dishes in town.

## Technologies used
* **Fragments** (especially when several fragments are displayed on a single page)
* **Google Firebase** : saving profiles (preferences, bookmarks, likes…) and translations
* **A RestFul API DB** : connection and interaction with an open sources and free database about recipes
sorted by different categories and preferably in different languages
* GPS tracker integration
* Camera integration
* Sharing on social networks (Share a recipe on Facebook, Instagram…)
* **Push notifications** with a closed app: scheduled reminders or position by GPS reminder
* Google calendar integration (ex.: plan a diner on a date)
* Email server for registration or link to print on screen

## Use cases

*1) Storing and listing Recipes*

* **1.1** Recipes populate sortable lists
* **1.2** Recipes can be search by their name or their ingredients
* **1.3** Recipes are sortable by difficulty, cost and time of preparation
* **1.4** Recipes come from a RestFul database and they are displayed in a short and a detailed view (aka fragments)

*2) User account and preferences*

* **2.1** An unauthenticated user have restricted functionalities (bookmarking, rating and planing are not allowed)
* **2.2** A user can log in with his email or his user name and his password (hash of passwords required and encryption too)
* **2.3** A user must validated his email to be able to log in
* **2.4** The system have to send an email with a random generated key to authenticate the new user
* **2.5** A font-end verification by Javascript should be done before the back-end verification calling the database
* **2.6** Preferences and data of each user are stored in a “SQL queries friendly” database (then not as objects)

*3) Device sensors and system integration*

* **3.1** The GPS trackers sends the position of the user by pinging with a long delay between each ping (avoid battery loss)
* **3.2** Push notifications are time framed or triggered at certain locations (without requiring the app to be on focus)
* **3.3** Integration with date picker and time picker to choose notifications
* **3.4** Integration with Google Calendar for saving planned recipes

*4) Back-office work (DB, server, Rest…)*
* **4.1** The Google Firebase Database stores users’s profiles and give permissions to connect
* **4.2** The Restful database is connected at the start of the session and provide 10 to 15 items on screens to display in lists.
* **4.3** Queries links profiles and users, users and bookmarked recipes, update rating of each recipes according to users’ votes… (worth to use the design patter Data Access Layer in OOP)
* **4.4** An email server sends emails to new users to confirm their registration
* **4.5** A random generated key is linked to the user and used to generated the link for validating users’s registration

## Rights of use

This is a **VIVES University College** (8500 Kortrijk, BE) project aimed for students to learn code
and share their knowledge.

I encourage you to review my code and fork it to make your own version.
I hope that it can you but please, if my code was useful to you, may you **quote 
my name** or put a **link to the original** repository?

Thank you for your comprehension ;)
#KeepCalmAndCode
