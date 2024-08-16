<img src="/screenshots/poc_granter.gif">

# Explicação
- Usamos Build.VERSION.SDK_INT para verificar se a versão do Android é maior ou igual a 31 (Android 12). Se for, usamos TelephonyCallback. Se não for, utilizamos o PhoneStateListener, que é compatível com versões anteriores.
- PhoneStateListener é uma API mais antiga que permite monitorar mudanças na força do sinal. No entanto, o valor retornado por getSignalStrength varia com o dispositivo e pode não ser tão bem normalizado quanto em versões mais novas.
- Precisão dos Níveis de Sinal: A API mais antiga (PhoneStateListener) pode não fornecer um nível de sinal tão preciso quanto a API mais nova, mas é a opção disponível em versões mais antigas do Android.