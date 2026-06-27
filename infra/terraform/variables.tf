variable "aws_region" {
  description = "AWS 리전"
  type        = string
  default     = "ap-northeast-2"
}

variable "project" {
  description = "프로젝트 이름 (리소스 태그/이름 접두사)"
  type        = string
  default     = "pinmoa"
}

variable "environment" {
  description = "배포 환경"
  type        = string
  default     = "dev"
}

# ── VPC ──────────────────────────────────────────────────────────────────────
variable "vpc_cidr" {
  description = "VPC CIDR 블록"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zones" {
  description = "사용할 가용 영역 (2개 이상 필요 - EKS/RDS 요구사항)"
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2c"]
}

variable "public_subnet_cidrs" {
  description = "퍼블릭 서브넷 CIDR (EKS 노드 배치, ALB)"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "프라이빗 서브넷 CIDR (RDS 전용)"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

# ── EKS ──────────────────────────────────────────────────────────────────────
variable "eks_cluster_version" {
  description = "EKS 쿠버네티스 버전"
  type        = string
  default     = "1.30"
}

variable "node_instance_type" {
  description = "EKS 워커 노드 EC2 인스턴스 타입"
  type        = string
  default     = "t3.small"
}

variable "node_desired_size" {
  description = "EKS 노드 희망 수 (비용 절감: 평소 1개)"
  type        = number
  default     = 1
}

variable "node_min_size" {
  description = "EKS 노드 최소 수"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "EKS 노드 최대 수"
  type        = number
  default     = 2
}

# ── RDS ──────────────────────────────────────────────────────────────────────
variable "db_name" {
  description = "PostgreSQL 데이터베이스 이름"
  type        = string
  default     = "pinmoa_dev"
}

variable "db_username" {
  description = "PostgreSQL 마스터 사용자"
  type        = string
  default     = "pinmoa"
}

variable "db_password" {
  description = "PostgreSQL 마스터 비밀번호 (terraform.tfvars 에서 설정)"
  type        = string
  sensitive   = true
}

# ── 앱 시크릿 ─────────────────────────────────────────────────────────────────
variable "jwt_secret" {
  description = "JWT 서명 시크릿 키 (최소 32자, terraform.tfvars 에서 설정)"
  type        = string
  sensitive   = true
}
