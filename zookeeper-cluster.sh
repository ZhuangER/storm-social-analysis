# download zookeeper
sudo wget http://apache.mirror.gtcomm.net/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
sudo tar -zxvf zookeeper-3.4.6.tar.gz
rm zookeeper-3.4.6.tar.gz

sudo mkdir /opt/zookeeper
sudo mv zookeeper-3.4.6 /opt/zookeeper
cd /opt/zookeeper/zookeeper-3.4.6
cp conf/zoo_sample.cfg conf/zoo.cfg
