# remote-log-watcher
A web application designed to collect log messages through UDP and display them on a web page in real-time.

## Overview
Ever wanted to monitor logs of specific applications via web browsers? Remote Log Watcher is here to help. It supports Java Log4j remote log appender and custom pre-formatted logs, making log monitoring accessible and straightforward.

## How to Run
1. Compile the application with Maven, which generates a .jar file.
2. Execute the .jar file using the following command:
```bash
java -jar remote-log-watcher.jar --spring.profiles.active=prod --server.port=80
```
3.Open your browser and connect to the server by entering a URL like http://localhost/#appId

## Java Log4j2 Example
To configure Java Log4j2 for remote log watching, use the following XML configuration:

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
To configure Unity for remote log watching, use the following C# script:

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

With this setup, you can easily monitor logs from Java and Unity applications directly in your web browser.

# License
This project is licensed under the terms of the MIT license.
