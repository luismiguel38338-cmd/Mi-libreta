package com.example.ui.util

object LegalContent {
    const val APP_VERSION = "1.0.0 (Estable)"

    val DISCLAIMER_SUMMARY = """
        Mi Libreta es una aplicación de gestión contable y administrativa interna para comerciantes, pequeños negocios y vendedores independientes.
        
        • NO es una entidad financiera, banco ni intermediario de préstamos regulado.
        • NO cobra intereses, comisiones bancarias ni emite tarjetas de crédito.
        • La aplicación funciona 100% desconectada (offline). Ningún dato personal o financiero de tus clientes se almacena en servidores externos ni se comparte con terceros.
        • Los acuerdos comerciales, plazos de pago y fidelidad de los registros son de exclusiva responsabilidad entre el comerciante y su cliente.
    """.trimIndent()

    val PRIVACY_POLICY = """
        POLÍTICA DE PRIVACIDAD Y PROTECCIÓN DE DATOS
        
        1. ALMACENAMIENTO LOCAL Y PRIVADO
        Toda la información registrada en esta aplicación (nombres de clientes, números de teléfono, notas de compras, deudas y abonos) reside única y exclusivamente en la memoria interna de su dispositivo móvil a través de una base de datos local SQLite.
        
        2. SIN ACCESO A INTERNET NI SERVIDORES
        La aplicación no envía, no sincroniza ni transmite información a ningún servidor remoto, empresa de analítica o servicio en la nube. Usted es el único dueño y custodio de sus registros comerciales.
        
        3. PERMISOS Y COMUNICACIÓN EXTERNA
        • Llamadas y WhatsApp: La aplicación solo interactúa con su aplicación de teléfono o WhatsApp cuando usted presiona explícitamente los botones de contacto, abriendo la aplicación externa con el mensaje o número que usted ha decidido gestionar.
        • Copias de Seguridad: Cuando usted decide exportar un archivo de respaldo o reporte CSV, dicho archivo es generado en su propio dispositivo y solo se comparte mediante las aplicaciones que usted seleccione voluntariamente (ej. Google Drive, correo o mensajería).
        
        4. RESPONSABILIDAD DEL USUARIO
        Es responsabilidad del comerciante mantener seguro su dispositivo móvil (contraseña, huella dactilar o PIN) y obtener el consentimiento básico de sus clientes para almacenar sus números de contacto conforme a las leyes aplicables de protección de datos personales.
    """.trimIndent()

    val TERMS_OF_SERVICE = """
        TÉRMINOS DE SERVICIO Y CONDICIONES DE USO
        
        1. NATURALEZA DE LA HERRAMIENTA
        Mi Libreta es un asistente de anotación contable sustitutivo de la libreta física de papel tradicional utilizada en colmados, abarrotes y comercios minoristas. No otorga garantías de cobro ni actúa como agencia de cobranzas.
        
        2. COMPROBANTES Y RECIBOS GENERADOS
        Los comprobantes emitidos en texto o compartidos por mensaje son resúmenes informativos de mutua conveniencia para el control de inventario y cuentas por cobrar. No constituyen pagarés notariales, cheques ni títulos ejecutivos bancarios.
        
        3. CÁLCULOS Y PRECISIÓN
        El usuario es responsable de verificar los montos ingresados al momento de registrar deudas o abonos. El saldo se calcula de forma automática como la suma estricta de deudas menos abonos registrados.
        
        4. EXONERACIÓN DE RESPONSABILIDAD
        Los desarrolladores de este software no se hacen responsables de pérdidas comerciales, disputas entre clientes y comerciantes, extravío del dispositivo sin copia de seguridad previa, ni por el uso indebido de la herramienta para fines contrarios a la ley.
    """.trimIndent()
}
