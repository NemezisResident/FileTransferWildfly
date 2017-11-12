package com.nemezis.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Nemezis
 * 
 *    Сервлет для скачивания файла с сервера Wildfly
 *
 */

@SuppressWarnings("serial")
public class FileDownload extends HttpServlet {

	// Константы
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

	// Домашний каталог для скачивания (устанавливаем вручную)
	private String filePath;

	// При создании инициализируем каталог
	public void init() throws ServletException {
		// this.filePath = "/etc/web_resource"; UNIX
		this.filePath = "C:/Users/Nemezis/Documents"; // MANUAL! -- set home catalog
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Получить запрошенный файл по информации о пути.
		String requestedFile = request.getPathInfo();

		// Проверяем, действительно ли файл указан в URI запроса.
		if (requestedFile == null) {
			// Выбрасываем  исключение , отправляем код 404.
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		// Декодируем имя файла (может содержать пробелы и включено) и подготавливаем файл
		// объект.
		File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));

		// Проверяем, действительно ли файл существует в файловой системе.
		if (!file.exists()) {
			// Выбрасываем  исключение , отправляем код 404 в случае отсутствия файла
			response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
			return;
		}

		// Получить тип содержимого по имени файла.
		String contentType = getServletContext().getMimeType(file.getName());

		// If content type is unknown, then set the default value.
		// For all content types, see:
		// http://www.w3schools.com/media/media_mimeref.asp
		// To add new content types, add new mime-mapping entry in web.xml.
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		// Инициализация ответа сервлета.
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
		response.setContentType(contentType);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

		// Подгатавливаем потоки.
		BufferedInputStream input = null;
		BufferedOutputStream output = null;

		try {

			// Открываем потоки.
			input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
			output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

			// Записываем содержимое файла в ответ.
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int length;

			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
		} finally {

			// Закрываем потоки.
			close(output);
			close(input);
		}
	}

	// Helpers (can be refactored to public utility class)
	private static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				// Обрабатываем ошибку
				e.printStackTrace();
			}
		}
	}
}