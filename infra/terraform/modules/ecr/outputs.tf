output "repository_urls" {
  description = "서비스별 ECR URL"
  value       = { for k, v in aws_ecr_repository.services : k => v.repository_url }
}
