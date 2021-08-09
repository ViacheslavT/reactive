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
Minimal console output information.
**NOTE:** all java apps were run with limited memory:
>java -Xmx512m -Xms512m -jar /app/app.jar

## WebFlux
### 1 user
```
checks.........................: 100.00% ✓ 281 ✗ 0
     data_received..................: 20 kB   12 kB/s
     data_sent......................: 27 kB   15 kB/s
   ✓ failed requests................: 0.00%   ✓ 0   ✗ 188
http_req_duration..............: avg=14.43ms  min=5.99ms   med=8.35ms   max=1.1s    p(90)=10.24ms  p(95)=11.66ms
       { expected_response:true }...: avg=14.43ms  min=5.99ms   med=8.35ms   max=1.1s    p(90)=10.24ms  p(95)=11.66ms
iteration_duration.............: avg=1.72s    min=1.72s    med=1.72s    max=1.72s   p(90)=1.72s    p(95)=1.72s
     iterations.....................: 1       0.578563/s
     vus............................: 1       min=1 max=1
     vus_max........................: 1       min=1 max=1
```
### 10 user
```
checks.........................: 100.00% ✓ 2732 ✗ 0
     data_received..................: 198 kB  48 kB/s
     data_sent......................: 259 kB  63 kB/s
   ✓ failed requests................: 0.00%   ✓ 0    ✗ 1828
http_req_duration..............: avg=29.21ms  min=4.99ms   med=14ms     max=3.56s    p(90)=26ms     p(95)=31ms
       { expected_response:true }...: avg=29.21ms  min=4.99ms   med=14ms     max=3.56s    p(90)=26ms     p(95)=31ms
iteration_duration.............: avg=3.3s     min=1.69s    med=3.5s     max=4.09s    p(90)=4.08s    p(95)=4.08s
     iterations.....................: 10      2.443436/s
     vus............................: 2       min=2  max=10
     vus_max........................: 10      min=10 max=10

```
### 100 user
```
checks.........................: 100.00% ✓ 6590  ✗ 0
     data_received..................: 604 kB  31 kB/s
     data_sent......................: 637 kB  32 kB/s
   ✓ failed requests................: 0.00%   ✓ 0     ✗ 4745
http_req_duration..............: avg=48.08ms  min=1ms      med=8.67ms max=6.5s    p(90)=18ms   p(95)=22.99ms
       { expected_response:true }...: avg=48.08ms  min=1ms      med=8.67ms max=6.5s    p(90)=18ms   p(95)=22.99ms
iteration_duration.............: avg=16.77s   min=870.97ms med=17.74s max=19.61s  p(90)=19.22s p(95)=19.44s
     iterations.....................: 100     5.097068/s
     vus............................: 16      min=16  max=99
     vus_max........................: 100     min=100 max=100
```
## MVC
### 1 user
```
checks.........................: 100.00% ✓ 266 ✗ 0
     data_received..................: 26 kB   4.9 kB/s
     data_sent......................: 25 kB   4.5 kB/s
   ✓ failed requests................: 0.00%   ✓ 0   ✗ 178
http_req_duration..............: avg=36.8ms   min=4.13ms   med=8.59ms   max=5.01s   p(90)=10.36ms  p(95)=11.16ms
       { expected_response:true }...: avg=36.8ms   min=4.13ms   med=8.59ms   max=5.01s   p(90)=10.36ms  p(95)=11.16ms
iteration_duration.............: avg=5.45s    min=5.45s    med=5.45s    max=5.45s   p(90)=5.45s    p(95)=5.45s
     iterations.....................: 1       0.183238/s
     vus............................: 1       min=1 max=1
     vus_max........................: 1       min=1 max=1
```
### 10 user
```
checks.........................: 100.00% ✓ 2708 ✗ 0
     data_received..................: 270 kB  5.3 kB/s
     data_sent......................: 251 kB  4.9 kB/s
   ✓ failed requests................: 0.00%   ✓ 0    ✗ 1812
http_req_duration..............: avg=200.72ms min=2.95ms  med=8.51ms   max=50.59s   p(90)=10.32ms  p(95)=11.56ms
       { expected_response:true }...: avg=200.72ms min=2.95ms  med=8.51ms   max=50.59s   p(90)=10.32ms  p(95)=11.56ms
iteration_duration.............: avg=35.15s   min=14.4s   med=37.16s   max=50.91s   p(90)=49.56s   p(95)=50.23s
     iterations.....................: 10      0.196414/s
     vus............................: 1       min=1  max=10
     vus_max........................: 10      min=10 max=10
```
### 100 user
```
checks.........................: 97.12% ✓ 3004  ✗ 89
     data_received..................: 302 kB 4.5 kB/s
     data_sent......................: 288 kB 4.3 kB/s
   ✓ failed requests................: 0.00%  ✓ 0     ✗ 2099
http_req_duration..............: avg=2.72s    min=2.98ms   med=8.43ms   max=59.99s   p(90)=11.26ms  p(95)=29.34ms
       { expected_response:true }...: avg=189.89ms min=2.98ms   med=8.14ms   max=56.57s   p(90)=10.52ms  p(95)=11.66ms
iteration_duration.............: avg=1m0s     min=9.84s    med=1m3s     max=1m4s     p(90)=1m3s     p(95)=1m3s
     iterations.....................: 100    1.503849/s
     vus............................: 15     min=15  max=100
     vus_max........................: 100    min=100 max=100
```
and new metric arrived :)
```
http_req_failed................: 4.24%  ✓ 89    ✗ 2010
```

## Results overwiev
