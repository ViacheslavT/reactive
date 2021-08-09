# reactive

Project contains 3 applications which should be run with MySql db as a separate container.
1) api - dummy api server which replicate 3rd party api and generates simple user information. Randomly generates delays and errors;
2) usual-api - api based on traditional spring mvc approach with data jpa;
3) webflux-api - api based on reactive Spring WebFLux approach and R2DBC driver.
4) rx-api - api based on rxjava framework. **NOT FINISHED and POSTPONED**, implementation of asynch rest client is required

# Run application
Go to "docker-compose" folder and execute command:
>docker-compose up --build

First build will take a while, all next will be fast.
Docker compose will build each application and run them in separate containers, MySql inclusive.
Applications can be run locally separately, just run MySQL container manually:
1) 
>docker run --name=mysqldb -d -p 3306:3306 -e MYSQL_ROOT_HOST=% -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=test -e MYSQL_PASSWORD=test -e MYSQL_DATABASE=test_db mysql/mysql-server:8.0
2) Run SpringBoot app **api**
3) Run other remained SpringBoot applications.

# Run k6 script
Once applications are started k6 scripts can be run.
Install K6 on local machine.
Go to "perf" folder and run one of the following scripts: **usual_test.js** or **webflux_test.js**
>k6 run usual_test.js

number of virtual users can be configured inside those scripts.
# Test logic
1) The script will execute main api endpoint **GET /retrieve**, which make 100 calls to **api** service. Usual api will use CompletableFuture and WebFLux will use Mono and all results will be merged into Flux.
2) Some object in the response will be with errors, that means that **api** service simulated errors, the number of success objects can be found in k6 logs.
After response is received, script will generate batch of save requests **POST /save**, one request per each object, this will simulate batch requests of simple UI application.
Each object will be saved into database. For usual api will be used JDBC driver for webflux api R2DBC.
3) After batch request executed (all of them should be success) the get request will be sent **GET /users**. Will be invoked JDBC for usual api to get list of users for current VU and R2DBC to get Flux of users for webflux api.
4) And the last step is generation of bath request to **DELETE /delete** to delete each user separately, 204 will be returned. JDBC used for usual api and R2DBC for webflux.

# k6 run results
## WebFlux
### 1 user
### 10 user
### 100 user

## MVC
### 1 user
### 10 user
### 100 user