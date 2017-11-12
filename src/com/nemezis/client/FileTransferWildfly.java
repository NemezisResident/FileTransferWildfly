package com.nemezis.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * @author Nemezis
 * 
 *    Класс точка входа приложения, инициализация
 *
 */

public class FileTransferWildfly implements EntryPoint {

	/**
	*  Создание прокси-сервера удаленной службы (RPC), для работы с сервером 
	* выполнение сервисов
	*/
	private final GeneralServiceAsync generalService = GWT.create(GeneralService.class);
	
	/**
	* Метод инициализации приложения (точка входа)
	 */
	public void onModuleLoad() {

		// Создаем виджет
		ContentW ContentW = new ContentW(generalService);

		// Добавляем его на страницу
		RootPanel.get().add(ContentW);
	}
}
