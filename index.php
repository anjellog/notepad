<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Speech to Text</title>
</head>
<body>
  <h1>Speech to Text</h1>
  <p id="output"></p>

  <script>
    var socket = new WebSocket("ws://localhost:8080");

    socket.onopen = function(e) {
        console.log("Connected to Java program");
    };

    socket.onmessage = function(event) {
        console.log("Received from Java:", event.data);
    };

    function sendTextToJava(text) {
        if(text.trim() !== '') {
            socket.send(text);
        }
    }

    let recognition;
    const output = document.getElementById('output');

    function startRecognition() {
      recognition = new window.webkitSpeechRecognition();
      recognition.lang = 'en-US';
      recognition.continuous = false;
      recognition.interimResults = true;

      recognition.start();
      output.innerHTML = '';

      recognition.addEventListener('result', (e) => {
        const transcript = Array.from(e.results)
          .map(result => result[0].transcript)
          .join('');

        output.innerHTML = transcript;

        if (typeof window.ReactNativeWebView !== 'undefined') {
          window.ReactNativeWebView.postMessage(transcript);
        }
      });

      recognition.addEventListener('end', () => {
        const finalTranscript = output.innerHTML.trim();
        if(finalTranscript !== '') {
            sendTextToJava(finalTranscript);
        }
        output.innerHTML += '<br>Recognition stopped.';
        startRecognition(); 
      });
    }

    startRecognition();
  </script>
</body>
</html>
