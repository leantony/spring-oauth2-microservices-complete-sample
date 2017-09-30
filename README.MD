# Spring microservices oauth2
A minimal config, that includes 3 microservices that employ spring oauth2 for security. The lack of straightfoward examples online led me to try this out for myself. The services are as follows;

## Auth service
This is based on [this example](https://github.com/leantony/spring-oauth-example/tree/master/oauth-server). I customized it however, for my own understanding. The service
does user authentication via oauth2, using preset client configurations. A logged in user can also add/remove the clients.

## Accounts service
I had a hard time configuring the auth server to be both an auth server, and a resource server. Hence, I decided to create a standalone service for user accounts. I totally understand that its possible to
merge this into the auth service, but I could not successfully do that.
This service returns user information, from a given a token.

## Dummy service
The name is abit off, but generally this service has a single endpoint, which just fetches user information from the accounts service, using remote token services and displays it as JSON.


# Points to note
+ I configured the services to use a mysql database. Please adjust this accordingly as you so wish
+ The sql files to create the tables are located in the shared folder.
+ The bcrypt passwords in the user's table are all similar. Just copy pasted. Am not creating users here. The original value for each is `123456`

# Functionality
## Normal Log in
Once the auth service is up and running, visit `http://localhost:8080/auth/login`. Once you login (password for all users is 123456), you
will be presented with a screen to modify the oauth stuff. Change as you so wish

## Testing of oauth2 clients

## Implicit grant
This follows the oauth2 implicit grant type. Where the client is a browser app for example, and it goes without saying that the secret 
cannot be plainly stored on it. Hence the client provides their `client_id`, `redirect_uri` and some `scopes`. The client id and redirect uri are used to authenticate the client. 
Now, enough talk. Just visit:

`http://localhost:8080/auth/oauth/authorize?client_id=exchange-agents&response_type=token&redirect_uri=http://www.google.com`

You will be redirected to the login page, if you havent logged in already. Dont worry about the client. If you did run the sql sample files I provided, this client exists. Otherwise, 
feel free to replace with your own.

**The apps are probably auto approved, so you wont see the ugly approval screen spring provides. Anyway, I know this as with many other spring aspects, can easily be customized**

## Fetching user from token
In doing the above correctly, you will be redirected to google, with an `#access_token` fragment appended to the url. Thats just how the implicit grant type works. You can 
copy the token from the url (don't worry. its not some big JWT. Its a short one) and run: 

```bash

curl --header "Authorization: Bearer <paste token here>" http://localhost:9000/accounts/users/me

```
You'll get that user's details as JSON.

## Accessing principal details within another service
Think of this as accessing sth like the /me endpoint on the facebook API. 
Our /me in this case is `http://localhost:9000/accounts/users/me`. In spring oauth, this is set via the configuration parameter `security.oauth2.resource.user-info-uri`. 
Once the `test-service` is up and running, visit:

```bash

curl --header "Authorization: Bearer <paste token here>" http://localhost:9500/dummy/data/principal

```

You should get the user's details as JSON. In this case, they are now part of the principal object, and can be used anywhere within the spring application if need be required.

So whats going on here??. I actually took some time to figure this out...
> The flow is as shown below
### Fetching user from token
+ Usr issues request
+ Service talks to auth-service. E.g to check if token exists, and has not expired, etc
+ Service finds the user info by the principal name

### Accessing principal details within another service
+ User issues request
+ Service talks to accounts-service. Which returns user information
+ Service decodes token and sets the principal object, using the user info


.....etc.