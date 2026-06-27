output "db_endpoint" {
  description = "RDS 접속 엔드포인트 (호스트:포트)"
  value       = aws_db_instance.postgres.endpoint
  sensitive   = true
}

output "db_host" {
  description = "RDS 호스트"
  value       = aws_db_instance.postgres.address
  sensitive   = true
}
