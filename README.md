# AI Sentiment Analysis on Kubernetes (Minikube)

## Описание проекта

Данный репозиторий содержит итоговый учебный проект по дисциплине  
**«Оркестрация и контейнеризация»**.

В рамках проекта разработано и развернуто контейнерное Java-приложение
для анализа тональности текста с использованием Kubernetes (Minikube).

Проект охватывает полный цикл работ:
- разработку REST API;
- контейнеризацию приложения;
- развертывание в Kubernetes;
- настройку балансировки нагрузки;
- автоскейлинг;
- мониторинг;
- аналитический обзор современных тенденций в области ИИ и контейнеризации.

---

## Цель проекта

Целью проекта является формирование практических навыков:

- разработки и контейнеризации Java-приложений;
- работы с Kubernetes и Minikube;
- настройки Service, Ingress и Horizontal Pod Autoscaler;
- интеграции мониторинга;
- анализа современных научных публикаций по тематике ИИ и оркестрации.

---

## Используемый стек технологий

- Java SE 17  
- Docker  
- Kubernetes  
- Minikube  
- NGINX Ingress Controller  
- Horizontal Pod Autoscaler (HPA)  
- Prometheus  
- Grafana  
- Helm  

---

## Архитектура решения

Архитектура проекта включает следующие компоненты:

- Java REST API для анализа тональности текста;
- Kubernetes Deployment с тремя репликами приложения;
- Service типа LoadBalancer для балансировки нагрузки;
- Ingress для маршрутизации HTTP-запросов;
- Horizontal Pod Autoscaler для автоматического масштабирования по CPU;
- Prometheus и Grafana для мониторинга метрик.

### Поток обработки запросов

Client → Ingress → Service → Pod (Sentiment API)


---

## Развертывание проекта

### Подготовка инфраструктуры

Запуск локального Kubernetes-кластера:

minikube start --cpus=4 --memory=8192
Проверка состояния:


minikube status
kubectl get nodes
Контейнеризация приложения
Сборка Docker-образа:


docker build -t sentiment-api:1.0 .
Загрузка образа в Minikube:


minikube image load sentiment-api:1.0
Развертывание в Kubernetes

Применение манифестов:

kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
kubectl apply -f k8s/hpa.yaml

Проверка развертывания:
kubectl get pods -n ai-demo
kubectl get svc -n ai-demo
kubectl get ingress -n ai-demo
kubectl get hpa -n ai-demo
Проверка работы приложения
Получение URL сервиса:


minikube service sentiment-api -n ai-demo --url
Пример HTTP-запроса:

curl "http://<URL>/api/sentiment?text=hello"

Мониторинг
Для мониторинга состояния приложения и кластера используется стек
Prometheus + Grafana.

Установка через Helm:

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update
helm install monitoring prometheus-community/kube-prometheus-stack
Мониторинг включает:

загрузку CPU подов;

количество реплик;

работу Horizontal Pod Autoscaler;

состояние Kubernetes-кластера.

Анализ тенденций в области ИИ и контейнеризации
В рамках проекта выполнен аналитический обзор научных публикаций arXiv
за 2024–2025 годы по следующим направлениям:

оптимизация распределения ресурсов для ИИ-нагрузок;

serverless-подходы к развертыванию машинного обучения;

применение методов reinforcement learning для планирования задач;

автоскейлинг инференса нейронных сетей;

использование ИИ как компонента оркестрации Kubernetes.

Подробный анализ представлен в отчете проекта.

Структура репозитория

.
├── sentiment-k8s-project/
│   ├── app/
│   ├── k8s/
│   ├── Dockerfile
│   └── README.md
└── README.md
Дополнительные материалы
отчет в формате DOCX / PDF;

презентация проекта;

скриншоты выполнения команд;

YAML-манифесты Kubernetes;

исходный код приложения.

