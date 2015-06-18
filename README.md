# remote-log-watcher
This web application has two roles. Collecting log messages through UDP and displaying the messages on the web page.

# Concern
Have you ever wanted to see logs of certain applications via web browsers? I made this application for that. 
It supports java log4j remote log appender and custom pre-formatted logs.

# How to execute
- Maven compile will generate .jar file. 
- Execute the jar file below commands.
```bash
java -jar remote-log-watcher.jar --spring.profiles.active=prod --server.port=80
```
- Open the browser and connect to the server by typing URL like http://localhost/#appId

# Java Log4j2 Example
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
	<Appenders>
		<Socket name="socket" host="replace here to server ip" port="7686" protocol="UDP">
			<SerializedLayout />
		</Socket>
	</Appenders>

	<Loggers>
		<Root level="info">
			<AppenderRef ref="socket"/>
		</Root>
	</Loggers>
</Configuration>
```

# Unity Example
```csharp
void Awake() 
{
    //Application.RegisterLogCallback(HandleLog);  //for unity 4.x
    
    Application.logMessageReceivedThreaded += HandleLog; // for unity 5.x
}

void HandleLog(string condition, string stackTrace, LogType type)
{
	string appId = "appid";
	System.Text.StringBuilder sb = new System.Text.StringBuilder();
	sb.Append("[").Append(appId).Append(";").Append(SystemInfo.deviceUniqueIdentifier).Append(";").Append(type.ToString()).Append("]");
	sb.Append(condition);
	if (type == LogType.Exception || type == LogType.Error)
	{
		sb.AppendLine(stackTrace);
	}
	using (System.Net.Sockets.UdpClient udpClient = new System.Net.Sockets.UdpClient())
	{
		udpClient.Connect(REMOTE_LOGGER_HOST, REMOTE_LOGGER_PORT);
		byte[] sendBytes = System.Text.Encoding.UTF8.GetBytes(sb.ToString());
		udpClient.Send(sendBytes, sendBytes.Length);
	}
}
```
