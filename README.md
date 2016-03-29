# storm-social-analysis
Real-time Social Analysis with Apache Storm


# Running Environment
## Standalone Mode
Ubuntu 14.04LTS


## Cluster
Built on Microsoft Azure HDInsight, Ubuntu 14.04LTS

# Components
- Apache Storm
- Twitter API
- Flask Web Framework
- Redis Key-Value Database
- D3.js (Data Driven Documents)


# Build Maps




# Usage
Launch website first
```
cd web
python app.py
```
Add Twitter's access token, access secert into TopNTweetTopology.java <br>
Submit topology to the storm nimbus
```
storm jar target/udacity-storm-lesson1_stage2-0.0.1-SNAPSHOT-jar-with-dependencies.jar udacity.storm.ReporterExclamationTopology
```


# Reference
