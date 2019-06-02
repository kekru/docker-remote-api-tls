#!/bin/sh

if [ -n $CREATE_CERTS_WITH_PW ]; then
  if [ -z "$(ls -A $CERTS_DIR)" ]; then

    echo "Create CA cert"
    /script/create-certs.sh -m ca -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 900
    echo "Create server cert"
    /script/create-certs.sh -m server -h $CERT_HOSTNAME -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 365
    echo "Create client cert"
    /script/create-certs.sh -m client -h testClient -pw $CREATE_CERTS_WITH_PW -t $CERTS_DIR -e 365

    mv $CERTS_DIR/ca.pem $CERTS_DIR/ca-cert.pem
  
  else
  
    echo "$CERTS_DIR is not empty. Not creating certs."
  fi
fi

exec "$@"