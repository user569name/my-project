from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # Локальный запуск: PostgreSQL из docker/docker-compose.yml на порту 5433.
    DATABASE_URL: str = "postgresql://tarot_user:tarot_password@localhost:5433/tarot_db"
    SECRET_KEY: str = "dev-secret-key-change-me"
    ALGORITHM: str = "HS256"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 10080
    OPENAI_API_KEY: str = ""

    class Config:
        env_file = ".env"


settings = Settings()
