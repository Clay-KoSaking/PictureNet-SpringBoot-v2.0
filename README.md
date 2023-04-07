# PictureNet-SpringBoot-v2.0

相比于 PictureNet-SpringBoot，此次 2.0 版本进行了如下优化：
1. 用户上传的图片统一存储在 `./image` 目录下，MySQL 中仅存储图片的相对路径；
2. 消息和评论的具体内容存储于 MongoDB，而其他细节继续存储于 MySQL ；
3. 在每个 Controller 类的每个方法中增加了对象锁，保证了并发操作的正确性。

***
注意事项：
1. 若要将项目部署至服务器上运行，则需在上传的 jar 包的同级目录中按 `./image` 的结构新建文件夹；
2. 需提前在 MongoDB 中新建数据库 picture_net。
