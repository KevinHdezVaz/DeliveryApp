package com.purificadora.notification;

/**
 * Constantes de notificaciones
 */
public class NotificationConstants {


    /**
     * El canal de la notificación
     */
    public static String CHANNEL_NAME = "CAFFENIO";
    public static String CHANNEL_SIREN_DESCRIPTION = "CAFFENIO";

    /**
     * Tipos de notificaciones
     */
    public static String TYPE_TAG = "TypeNotification";
    public static String CANCEL_ORDER = "CANCEL_ORDER";
    public static String SURVEY = "SURVEY";
    public static String GENERAL = "GENERAL";

    /**
     * Tipos de acciones de una notificación
     */
    public final static int actionScreen = 1;
    public final static int actionLink = 2;
    public final static int actionDetailNotification = 3;

}
