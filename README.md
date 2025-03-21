# **Documentação do Projeto - Controle de Volume**

## **1. Introdução**
Este projeto é um aplicativo Android para controle de volume e modos de som do dispositivo. Ele permite ao usuário ajustar o volume do toque, ativar o modo silencioso, vibrar ou normal, e mutar o som facilmente.

## **2. Funcionalidades**
- Ajuste do volume de toque por meio de um **SeekBar**.
- Controle de **Mute** através de um **Switch**.
- Alternação entre os modos **Normal, Silencioso e Vibrar** via **RadioButtons**.
- Sincronização entre os controles do app e o sistema operacional.

## **3. Tecnologias Utilizadas**
- **Linguagem:** Java
- **Framework:** Android SDK
- **Armazenamento:** SharedPreferences
- **Gerenciamento de áudio:** AudioManager

## **4. Estrutura do Código**
### **4.1. MainActivity.java**
Responsável por:
- Gerenciar os componentes da interface.
- Controlar o volume e os modos de som.
- Garantir sincronização entre os estados do dispositivo e a interface do usuário.

### **4.2. activity_main.xml**
Define a interface do usuário, contendo:
- **SeekBar** para controle de volume.
- **Switch** para mutar o som.
- **RadioGroup** para alternar entre os modos de som.

### **4.3. AndroidManifest.xml**
Adiciona as permissões necessárias:
```xml
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />
```

## **5. Permissões Necessárias**
Para alterar o modo "Não Perturbe" (DND), o usuário precisa conceder permissão manualmente. O app solicita permissão através do seguinte código:
```java
NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
if (!notificationManager.isNotificationPolicyAccessGranted()) {
    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
    startActivity(intent);
}
```

## **6. Como Usar**
1. **Ajustar volume:** Use o **SeekBar** para aumentar ou diminuir o volume.
2. **Mutar o som:** Ative ou desative o **Switch** para silenciar o dispositivo.
3. **Alterar modo de som:** Escolha entre **Normal, Vibrar ou Silencioso** usando os botões de opção.
4. **Permissão para Silencioso/Vibrar:** Caso o modo "Silencioso" não funcione, conceda permissão nas configurações do dispositivo.

## **7. Melhorias Futuras**
- Implementar suporte para controle de volume de mídia.
- Adicionar um widget para controle rápido na tela inicial.
- Personalização do design para uma melhor experiência do usuário.

---
**Autor:** Desenvolvedor do projeto.
**Data da última atualização:** 18 de março de 2025.

