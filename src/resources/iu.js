var peerConnections = {};
var websocketAudio;
var from_id;
var from_name;

var to_id;
var constraints = {
    video: false,
    audio: true
};
// Using free public google STUN server.
var configuration = {
    iceServers: [{
            urls: 'stun:stun.l.google.com:19302'
        }]
}
function joinAudio(r_id) {
    room_id = r_id;
    $(`#audio_room_${r_id} div`).remove();
    $(`#audio_room_${r_id}`).append(
            `<div class="row mt-4">` +
            `<h5>You</h5>` +
            `<audio id="localAudio"controls autoplay volume="true" muted></audio>` +
            `</div>` +
            `<div class="row">` +
            `<div class="col" id="remoteUser">` +
            `</div>` +
            `</div>`
            );




    navigator.mediaDevices.getUserMedia(constraints)
            .then(stream => {
                localAudio.srcObject = stream;
                localStream = stream;
                initWebsocket();
            });
}
function initWebsocket() {
    websocketAudio = new WebSocket("ws://localhost:8081/audio");
    websocketAudio.onopen = function () {
        sendAudioMessage(-1, null);
    };
    websocketAudio.onmessage = async function (msg) {
        var message = JSON.parse(msg.data);
        var users; // list user in room audio
        users = message.users;
        from_id = message.user_id;
        from_name = message.user_name;
        if (message.type === 'user-joined') {
            console.log("JOIN");
            console.log(message);
            handleJoin(users);
        } else if (message.type === 'signal') {
            console.log("SIGNAL");
            handleSignal(message.signal);
        }
    }
}
function handleJoin(users) {
    const peerConnection = new RTCPeerConnection(configuration);
    peerConnections[from_id] = peerConnection;

    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    peerConnection.onicecandidate = event => {
        if (event.candidate) {
            sendAudioMessage(from_id, event)
        }
    };

    peerConnection
            .createOffer()
            .then(sdp => peerConnection.setLocalDescription(sdp))
            .then(() => {
                sendAudioMessage(from_id, peerConnection.localDescription);
            });
//                users.forEach(function (user_id) {
//                    if (!peerConnections[user_id]) {
//
//                        // Prepare peer connection object
//                        peerConnections[user_id] = new RTCPeerConnection(configuration);
//                        //Wait for their ice candidate   
//                        peerConnections[user_id].onicecandidate = function (event) {
//                            if (event.candidate != null) {
//                                console.log('SENDING ICE');
//                                sendAudioMessage(user_id, event);
//                            }
//                        }
//                        //Wait for their video stream
//                        peerConnections[user_id].ontrack = displayRemoteStream;
//                        //Add the local video stream                                
//                        localStream.getTracks().forEach(track => peerConnections[user_id].addTrack(track, localStream));
//                    }
//                });
//                //Create an offer to connect with your local description
//                if (users.length > 1) {
//                    console.log(from_name + " CREATE OFFER");                    
//                    const offer = peerConnections[from_id].createOffer();
//                    console.log(peerConnections[from_id]);
//                    peerConnections[from_id].setLocalDescription(offer);
//                    console.log(peerConnections[from_id].localDescription);
//                    //console.log(peerConnections[from_id].localDescription);
//                    sendAudioMessage(from_id, peerConnections[from_id].localDescription);
//                }

}
function handleSignal(signal) {
    //Make sure it's not coming from yourself
    console.log(from_id + "->" + user.id);
    console.log(signal);

    if (signal) {
        switch (signal.type) {
            case "offer":
                handleOfferSignal(signal);
                break;
            case "answer":
                handleAnswerSignal(signal);
                break;
                // In local network, ICE candidates might not be generated.
            case "candidate":
                handleIceCandidateSignal(signal);
                break;
            default:
                break;
        }
    }


}
function sendAudioMessage(to_id, sdp) {
    var message = {
        room_id: room_id,
        user_id: user.id,
        user_name: user.name,
        to_id: to_id,
        sdp: sdp
    };
    websocketAudio.send(JSON.stringify(message));
}
function preparepeerConnection(from_id) {

}

function handleOfferSignal(description) {
    console.log("HANDLE OFFER:");
    console.log(description);
    var peerConnection = new RTCPeerConnection(configuration);

    peerConnection
            .setRemoteDescription(new RTCSessionDescription(description))
            .then(() => peerConnection.createAnswer())
            .then(sdp => peerConnection.setLocalDescription(sdp))
            .then(() => {
                sendAudioMessage(from_id, peerConnection.localDescription);
            });
    peerConnection.ontrack = event => {
        displayRemoteStream(event);
    };
    peerConnection.onicecandidate = event => {
        if (event.candidate) {
            sendAudioMessage(from_id, event.candidate);
        }
    };
}
function handleAnswerSignal(answer) {
    console.log("HANDLE OFFER:");
    console.log(answer);
    peerConnections[from_id].setRemoteDescription(new RTCSessionDescription(answer));
}
function handleIceCandidateSignal(cadidate) {
    console.log("handle cadidate: " + candidate);
    peerConnections[from_id].addIceCandidate(new RTCIceCandidate(cadidate)).catch(e => console.log(e));
}
function displayRemoteStream(event) {
    console.log(localStream);
    console.log(event.streams[0]);
    $('#remoteUser').
            append(
                    `<div class="row">` +
                    `<h5>${from_name}</h5>` +
                    `<audio class="remote-audio" id="audio${from_id}" controls autoplay volume="true"></audio>` +
                    `</div>`);
    var remoteAudio = document.getElementById(`audio${from_id}`);
    remoteAudio.autoplay = true;
    remoteAudio.playsinline = true
    console.log(remoteAudio);
    remoteAudio.srcObject = event.streams[0];
    //localAudio.srcObject = event.streams[0];
}