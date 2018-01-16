## react native compress images


----------
A React Native module that allows you to compress you images.( currently just support android).

#### install

yarn add react-native-compress-images
#####Automatic Installation
react-native link
####Usage
1, import CompressImages from 'react-native-compress-images';
2, 
```
CompressImages({
				urlList: ['storage/emulated/0/Pictures/image-b1ae0ff3-c014-4e37-b510-f00d6ec88b08.jpg']
              }).then((res) => {
	              //compression image path list
                  console.log(res.data);
              })
```              
####Options

| option     |   Info   |
| :-------- |:------: |
| urlList    | image path array。just like this: ['storage/emulated/0/Pictures/image-b1ae0ff3-c014-4e37-b510-f00d6ec88b08.jpg', 'image2.png']  |
|maxWidth| definition compress image maxWidth. default : 380|
|maxHeight| definition compress image maxHeight. default : 600|
|quality| 0~100  default: 60|
####返回参数说明
返回的参数
```res.data```
为压缩后的图片地址列表。文件名称与urlList不一致但是与urlList一一对应。

####注意
重复调用，压缩后的图片会覆盖上一次压缩的图片。所以确保上一次压缩的图片已无效。
       