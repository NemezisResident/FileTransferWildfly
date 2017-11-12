package com.nemezis.server;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * 
 * @author Nemezis
 *
 *         Сервлет для загрузки файла на сервер Wildfly
 */

@SuppressWarnings("serial")
public class FileUpload extends HttpServlet {

	// Домашний каталог (Устанавливаем вручную)
	private String filePath;

	// Имя файла
	public String fileNameOld;
	private String fileNameNew;

	// Легирование на сервер
	private static final Logger LOG = Logger.getLogger(FileUpload.class);

	// При создании
	public void init() throws ServletException {
		// this.filePath = "/etc/web_resource/"; UNIX
		this.filePath = "C:/Users/Nemezis/Documents/"; // MANUAL!! - set home directory

	}

	// Вызываемый метод
	@SuppressWarnings("unused")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		// Создаем новый обработчик переноса файлов
		ServletFileUpload upload = new ServletFileUpload();

		// Обработать входной поток
		ByteArrayOutputStream out = null;

		try {

			// Парсим запрос
			FileItemIterator iter = upload.getItemIterator(request);

			// Перебираем загруженное файлы
			while (iter.hasNext()) {

				FileItemStream item = iter.next();
				String name = item.getFieldName();

				InputStream stream = item.openStream();

				// Если параметр то
				if (item.isFormField()) {
					// Получаем название файла
					fileNameOld = Streams.asString(stream).trim();  // старый вариант получения названия файла
					LOG.info("OLD ver File: " + filePath + fileNameOld);
					
				// Если файл
				} else {

					// Обрабатываем входной поток (файл)
					out = new ByteArrayOutputStream();
					int len;
					byte[] buffer = new byte[8192];
					while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, len);
					}

					// Получаем имя файла
					fileNameNew = FilenameUtils.getName(item.getName());
					LOG.info("EXEC File: " + filePath + fileNameNew);
				}

				// Закрываем ресурсы
				stream.close();
			}

			// Записываем файл
			try (OutputStream outputStream = new FileOutputStream(filePath + fileNameNew)) {
				out.writeTo(outputStream);

				// Закрываем ресурсы
				out.close();
				outputStream.close();
			}
		} catch (Exception e) {
			LOG.info("WARNING ERROR: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
