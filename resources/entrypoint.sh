#!/bin/sh

if [ -n $CREATE_CERTS_WITH_PW ]; then
  if [ -z "$(ls -A $CERTS_DIR)" ]; then

    echo "Create CA cert"
    /script/create-certs.sh -m ca -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 900
    echo "Create server cert"
    /script/create-certs.sh -m server -h $CERT_HOSTNAME -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 365
    echo "Create client cert"
    /script/create-certs.sh -m client -h testClient -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 365

    mkdir $CERTS_DIR/client
    mv $CERTS_DIR/ca.pem $CERTS_DIR/ca-cert.pem
    cp $CERTS_DIR/ca-cert.pem $CERTS_DIR/client/ca.pem
    mv $CERTS_DIR/client-testClient-cert.pem $CERTS_DIR/client/cert.pem
    mv $CERTS_DIR/client-testClient-key.pem $CERTS_DIR/client/key.pem
    chmod 444 $CERTS_DIR/client/key.pem

  else
  
    echo "$CERTS_DIR is not empty. Not creating certs."
  fi
fi

exec "$@"