//thrift --gen java -out ../kotlin service.thrift
namespace java top.abosen.toys.ipv6.thrift

service HelloService{
    string hello()
}
