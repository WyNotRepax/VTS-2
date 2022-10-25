/* Pub-Sub-Server  
 * Getestet unter Ubuntu 20.04 64 Bit / g++ 9.3
 */

#include <iostream>
#include <memory>
#include <string>
#include <fstream>
#include <set>

#include <grpcpp/grpcpp.h>
#include <grpcpp/health_check_service_interface.h>
#include <grpcpp/ext/proto_server_reflection_plugin.h>

// Diese Includes werden generiert.
#include "pub_sub.grpc.pb.h"
#include "pub_sub_deliv.grpc.pb.h"
#include "pub_sub_config.h"

// Notwendige gRPC Klassen.
using grpc::Server;
using grpc::ServerBuilder;
using grpc::ServerContext;

using grpc::Channel;
using grpc::ClientContext;
using grpc::Status;

// Diese Klassen sind in den .proto Dateien definiert.
using pubsub::EmptyMessage;
using pubsub::Message;

using pubsub::PubSubDelivService;
using pubsub::PubSubService;
using pubsub::ReturnCode;
using pubsub::SubscriberAddress;
using pubsub::Topic;

// Implementierung des Service
class PubSubServiceImpl final : public PubSubService::Service
{
  // TODO: Channel topic und Subscribers für diesen Server merken
  // ...
  std::string topic;

  std::map<std::string,std::unique_ptr<PubSubDelivService::Stub>> subscribers;



  static std::string stringify(const SubscriberAddress &adr)
  {
    std::string s = adr.ip_address() + ":";
    s += std::to_string (adr.port());
    return s;
  }

  Status subscribe(ServerContext *context, const SubscriberAddress *request,
                   ReturnCode *reply) override
  {
    std::string receiver = stringify(*request);
    bool created = subscribers.emplace(receiver, PubSubDelivService::NewStub(grpc::CreateChannel(receiver, grpc::InsecureChannelCredentials()))).second;
    if(created){
      reply->set_value(pubsub::ReturnCode_Values_OK);
    }else{
      reply->set_value(pubsub::ReturnCode_Values_CANNOT_REGISTER);
    }
    return Status::OK;
  }

  Status unsubscribe(ServerContext *context, const SubscriberAddress *request,
                     ReturnCode *reply) override
  {
    std::string receiver = stringify(*request);
    int removed = subscribers.erase(receiver);
    if(removed > 0){
       reply->set_value(pubsub::ReturnCode_Values_OK);
    }else{
      reply->set_value(pubsub::ReturnCode_Values_CANNOT_UNREGISTER);
    }
    return Status::OK;
  }

  void handle_status(const std::string operation, Status &status)
  {
    // Status auswerten -> deliver() gibt keinen Status zurück,k deshalb nur RPC Fehler melden.
    if (!status.ok()) {
      std::cout << "[ RPC error: " << status.error_code() << " (" << status.error_message()
                << ") ]" << std::endl;
    }
  }

  Status publish(ServerContext *context, const Message *request,
                 ReturnCode *reply) override
  {
    // TODO: Nachricht an alle Subscriber verteilen
    ClientContext clientContext;
    EmptyMessage empty;
    Message requestOut;
    requestOut.set_message((topic + ": " + request->message()));
    for (auto& subscriberPair : subscribers) {
       Status status = subscriberPair.second->deliver(&clientContext, requestOut, &empty);
       handle_status("deliver()",status); 
    }
    reply->set_value(pubsub::ReturnCode_Values_OK);
    return Status::OK;
  }

  Status set_topic(ServerContext *context, const Topic *request,
                     ReturnCode *reply) override
  {
    if(request->passcode().compare(PASSCODE) == 0){
      // TODO: Topic setzen und Info ausgeben
      topic = request->topic();
      reply->set_value(pubsub::ReturnCode_Values_OK);

      Message message;
      ReturnCode publishReply;
      message.set_message(std::string("Topic Changed to ") + topic);
      publish(context,&message,&publishReply);
    }
    else{
      reply->set_value(pubsub::ReturnCode_Values_CANNOT_SET_TOPIC);
    }
    

    return Status::OK;
  }

public:
  PubSubServiceImpl()
  {
    // TODO: Topic initialisieren
        topic = "<no topic set>";
  }
};

void RunServer()
{
  // Server auf dem lokalen Host starten.
  // std::string server_address(PUBSUB_SERVER_IP);
  std::string server_address("0.0.0.0"); // muss der lokale Rechner sein
  server_address += ":";
  server_address += std::to_string(PUBSUB_SERVER_PORT); // Port könnte umkonfiguriert werden

  PubSubServiceImpl service;

  grpc::EnableDefaultHealthCheckService(true);
  grpc::reflection::InitProtoReflectionServerBuilderPlugin();
  ServerBuilder builder;
  // Server starten ohne Authentifizierung
  builder.AddListeningPort(server_address, grpc::InsecureServerCredentials());
  // Registrierung als synchroner Dienst
  builder.RegisterService(&service);
  // Server starten
  std::unique_ptr<Server> server(builder.BuildAndStart());
  std::cout << "[ Server launched on " << server_address << " ]" << std::endl;

  // Warten auf das Ende Servers. Das muss durch einen anderen Thread
  // ausgeloest werden.  Alternativ kann der ganze Prozess beendet werden.
  server->Wait();
}

int main(int argc, char **argv)
{
  // Server starten
  RunServer();
  return 0;
}