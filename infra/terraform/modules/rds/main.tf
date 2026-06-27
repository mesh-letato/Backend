# ── 보안그룹: EKS 노드 → RDS 5432 만 허용 ──────────────────────────────────
resource "aws_security_group" "rds" {
  name        = "${var.project}-rds-sg"
  description = "RDS PostgreSQL security group - EKS nodes only"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Allow PostgreSQL from EKS nodes only"
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [var.eks_node_sg_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project}-rds-sg" }
}

# ── RDS 서브넷 그룹 (프라이빗 서브넷 2개 이상 필요) ─────────────────────────
resource "aws_db_subnet_group" "main" {
  name        = "${var.project}-db-subnet-group"
  subnet_ids  = var.private_subnet_ids
  description = "Pinmoa RDS subnet group - private"
}

# ── RDS PostgreSQL 인스턴스 ───────────────────────────────────────────────────
resource "aws_db_instance" "postgres" {
  identifier        = "${var.project}-postgres"
  engine            = "postgres"
  engine_version    = "16"
  instance_class    = "db.t3.micro"
  allocated_storage = 20
  storage_type      = "gp2"

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  # 외부 인터넷에서 직접 접근 불가
  publicly_accessible = false

  # 단일 AZ (비용 절감 - 프로덕션에서는 multi_az = true 권장)
  multi_az = false

  # 자동 백업 비활성화 (비용 절감 - 개발 환경용)
  backup_retention_period = 0

  # terraform destroy 시 스냅샷 없이 바로 삭제 (개발 환경용)
  skip_final_snapshot = true

  # 마이너 버전 자동 업그레이드
  auto_minor_version_upgrade = true

  tags = { Name = "${var.project}-postgres" }
}
