generate_keys 在mac环境下双击该文件会自动生成raskey目录，并且会生成公钥私钥文件。其中：
private_key.pem、public_key.pem  是OC使用的文件，将其拷贝到工程中就OK了
rsa_public_key.pem、pkcs8_private_key.pem  这个是java需要使用的文件，将其添加到WEB-INF目录下

后端是使用java web工程，使用tomcat部署即可。

下面代码测试结果 箭头指向的解密方 另一方是加密方
  OC           java
public   <-   private
private  <-   public
public   ->   private
private  ->   public 
