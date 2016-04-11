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
d3.js
![](https://github.com/ZhuangER/storm-social-analysis/blob/master/map_visualization.png)




# Usage
Launch website first
```
cd web
python app.py
```
Add Twitter's access token, access secert into TopNTweetTopology.java <br>
Submit topology to the storm nimbus
```
storm jar target/yu-storm-hack-0.0.1-SNAPSHOT-jar-with-dependencies.jar yu.storm.TweetTopology
```


# Reference
https://github.com/udacity/ud381/tree/master/lesson4/TeamAwesome
