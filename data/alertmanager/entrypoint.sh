#!/bin/sh
set -e

# Replace placeholders with environment variable values in the config file
sed -i "s|\${ALERT_RESOLVE_TIMEOUT}|${ALERT_RESOLVE_TIMEOUT}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${SMTP_SMARTHOST}|${SMTP_SMARTHOST}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${SMTP_FROM}|${SMTP_FROM}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${SMTP_AUTH_USERNAME}|${SMTP_AUTH_USERNAME}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${SMTP_AUTH_PASSWORD}|${SMTP_AUTH_PASSWORD}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${SMTP_REQUIRE_TLS}|${SMTP_REQUIRE_TLS}|g" /etc/alertmanager/alertmanager.yml
sed -i "s|\${ALERT_EMAIL_TO}|${ALERT_EMAIL_TO}|g" /etc/alertmanager/alertmanager.yml

# Start Alertmanager
exec alertmanager --config.file=/etc/alertmanager/alertmanager.yml
