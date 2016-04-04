# storm-social-analysis
Real-time Social Analysis with Apache Storm


# Running Environment
## Standalone Mode
Ubuntu 14.04LTS


## Cluster
Built on AWS, base image Ubuntu 14.04LTS <br/>
zookeeper share physcial machine with nimbus and supervisor
- 3 zookeeper nodes
- 2 supervisor
- 1 nimbus



# Components
- Apache Storm
- Twitter API
- Flask Web Framework
- Redis Key-Value Database
- D3.js (Data Driven Documents)


# Build Maps

Map standard: Mercator
Map scope: world

# visualization
basic map 
image

Bubbles




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
