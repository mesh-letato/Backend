output "ecr_urls" {
  description = "ECR 리포지토리 URL (docker push 시 사용)"
  value       = module.ecr.repository_urls
}

output "eks_cluster_name" {
  description = "EKS 클러스터 이름 (kubectl 연결 시 사용)"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "EKS API 엔드포인트"
  value       = module.eks.cluster_endpoint
}

output "rds_endpoint" {
  description = "RDS 엔드포인트 (K8s Secret 설정 시 사용)"
  value       = module.rds.db_endpoint
  sensitive   = true
}

output "aws_region" {
  description = "AWS 리전"
  value       = var.aws_region
}

output "kubeconfig_command" {
  description = "kubeconfig 업데이트 명령어"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${module.eks.cluster_name}"
}
