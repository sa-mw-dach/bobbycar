FROM registry.access.redhat.com/ubi8/nginx-118

COPY ./www/ .
COPY nginx.conf /etc/nginx/nginx.conf

#Run script uses standard ways to run the application
CMD nginx -g "daemon off;"