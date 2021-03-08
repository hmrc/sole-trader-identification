
# Sole Trader Identification

### How to run the service
1. Make sure any dependent services are running using the following service-manager command `sm --start SOLE_TRADER_IDENTIFICATION_ALL`
2. Stop the frontend in service manager using `sm --stop SOLE_TRADER_IDENTIFICATION`
3. Run the frontend locally using
   `sbt 'run 9721 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
