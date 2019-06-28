package com.jt.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jt.vo.ImageVO;

@Service
@PropertySource("classpath:/properties/image.properties")
public class FileServiceImpl implements FileService{
	//定义本地磁盘的路径
	@Value("${image.localDirPath}")
	private String localDirPath;
	//定义虚拟路径的名称
	@Value("${image.urlPath}")
	private String urlPath;

	/**
	 * 1.获取图片名称
	 * 2.校验是否为图片类型 jpg|png|gif
	 * 3.校验是否为恶意程序  木马.exe.jpg
	 * 4.准备文件夹,	分文件保存  按照时间存储 yyyy/MM/dd
	 * 5.防止文件重名	.UUID 32位16进制数+毫秒数
	 * 
	 * 正则常用字符:
	 * 1.^	 标识...开始
	 * 2.$ 	结束
	 * 3.点. 任意单个字符
	 * 4.*	表示任意个0~~~无穷
	 * 5.+	表示任意个1~~~无穷
	 * 6.\. 标识特殊字符.
	 * 7.(xxx|xxx|xxx)
	 */
	@Override
	public ImageVO updateFile(MultipartFile uploadFile) {
		ImageVO imageVO=new ImageVO();
		//1.获取图片名称	1.jpg
		String fileName = uploadFile.getOriginalFilename();
		//转成小写
		fileName = fileName.toLowerCase();

		//2.校验图片类型	使用正则表达式判断字符串
		if(!fileName.matches("^.+\\.(jpg|png|gif)$")) {
			imageVO.setError(1);//表示上传有误
			return imageVO;
		}
		//3.判断是否为恶意程序  图片模板
		try {
			BufferedImage bufferedImage=
					ImageIO.read(uploadFile.getInputStream());
			int width=bufferedImage.getWidth();
			int height = bufferedImage.getHeight();

			if(width==0||height==0) {
				imageVO.setError(1);
				return imageVO;

			}
			//4.时间转化为字符串 2019/5/31
			String dateDir = 
					new SimpleDateFormat("yyyy/MM/dd").format(new Date());

			//5.准备文件夹  E:/jt_test/file/+yyyy/MM/dd
			
			String localDir=localDirPath +dateDir;
			File dirFile = new File(localDir);
			if(!dirFile.exists()) {
				//如果文件不存在,则创建文件夹
				dirFile.mkdirs();
			}
			//6.使用UUID定义文件名称uuid.jpg
			String uuid=UUID.randomUUID().toString().replace("-", "");
			//图片类型 a.jpg 动态获取".jpg"
			String fileType=fileName.substring(fileName.lastIndexOf("."));

			//拼接新的文件名称
			String realLocalPath=localDir+"/"+uuid+fileType;

			//7.完成文件上传
			uploadFile.transferTo(new File(realLocalPath));
			
			//8.拼接url路径 	http://image.jt.com/yyyy/MM/dd
			String realUrlPath=urlPath+dateDir+"/"+uuid+fileType;
			
			//将文件信息回传给页面
			imageVO.setError(0)
					.setHeight(height)
					.setWidth(width)
					//.setUrl("https://m.360buyimg.com/babel/jfs/t1/73001/32/715/130785/5cef599aEf2ecae32/b8f310a27c78e987.jpg");	
					.setUrl(realUrlPath);
			
		}catch (Exception e) {
			e.printStackTrace();
			imageVO.setError(1);
			return imageVO;
		}


		return imageVO;
	}

}
