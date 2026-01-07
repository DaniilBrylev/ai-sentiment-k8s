# Sentiment K8s Project

Контейнерное Java-приложение с REST API для mock-анализа тональности текста, готовое к запуску в Minikube.

## Быстрый старт

Все команды выполняются из каталога `sentiment-k8s-project`.

### 1) Запуск Minikube

```bash
minikube start --cpus=4 --memory=8192mb --nodes=2
```

Ожидаемо: кластер запущен.

### 2) Включить Ingress

```bash
minikube addons enable ingress
```

Ожидаемо: addon ingress включен.

Если HPA не показывает метрики, включите metrics-server:

```bash
minikube addons enable metrics-server
```

### 3) Сборка Docker-образа ВНУТРИ Minikube

Linux/macOS:

```bash
eval $(minikube docker-env)
```

Windows PowerShell:

```powershell
minikube docker-env | Invoke-Expression
```

Сборка образа:

```bash
docker build -t sentiment-api:1.0 .
```

Или через скрипт:

```bash
./scripts/build.sh
```

Ожидаемо: образ `sentiment-api:1.0` собран без ошибок.

### 4) Деплой в Kubernetes

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

Или через скрипт:

```bash
./scripts/deploy.sh
```

Ожидаемо: ресурсы созданы/обновлены.

### 5) Проверка состояния ресурсов

```bash
kubectl get pods -n ai-demo
kubectl get svc -n ai-demo
kubectl get ingress -n ai-demo
kubectl get hpa -n ai-demo
```

Ожидаемо: 3 pod в статусе Running, сервис типа LoadBalancer, ingress создан, HPA отображается.

## Проверка API через Service (LoadBalancer)

Получить URL сервиса:

```bash
minikube service sentiment-api -n ai-demo --url
```

Пример проверки:

```bash
curl "<SERVICE_URL>/api/sentiment?text=hello"
```

Ожидаемо:

```json
{"text":"hello","sentiment":"positive","score":0.87}
```

Проверка health:

```bash
curl "<SERVICE_URL>/health"
```

Ожидаемо:

```
OK
```

## Проверка API через Ingress

Linux/macOS:

```bash
MINIKUBE_IP=$(minikube ip)
curl "http://$MINIKUBE_IP/api/sentiment?text=hello"
```

Windows PowerShell:

```powershell
$minikubeIp = minikube ip
curl "http://$minikubeIp/api/sentiment?text=hello"
```

Ожидаемо:

```json
{"text":"hello","sentiment":"positive","score":0.87}
```

## Мониторинг (Prometheus/Grafana)

Приложение отдает метрики на `GET /metrics`, а в Deployment добавлены аннотации для Prometheus:

- `prometheus.io/scrape: "true"`
- `prometheus.io/port: "8080"`
- `prometheus.io/path: "/metrics"`

## Какие команды скриншотить для отчёта

```bash
minikube start --cpus=4 --memory=8192mb --nodes=2
minikube addons enable ingress
```

```bash
docker build -t sentiment-api:1.0 .
```

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml
```

```bash
kubectl get pods -n ai-demo
kubectl get svc -n ai-demo
kubectl get ingress -n ai-demo
kubectl get hpa -n ai-demo
```

```bash
minikube service sentiment-api -n ai-demo --url
```

```bash
curl "<SERVICE_URL>/api/sentiment?text=hello"
```

```bash
curl "<SERVICE_URL>/health"
```

```bash
MINIKUBE_IP=$(minikube ip)
curl "http://$MINIKUBE_IP/api/sentiment?text=hello"
```
