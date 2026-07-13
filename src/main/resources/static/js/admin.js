
const modal = ducument.getElementById('myModal');
const deleteBtn = document.getElementById('deleteBtn');
const cancelBtn = document.getElementById('cancelBtn');
const confirmBtn = document.getElementById('confirmBtn');

// 삭제 버튼 클릭시 모달 열기
deleteBtn.addEventListener('click', function(){
	modal.style.display = 'flex';
});

// 취소 버튼 클릭시 모달 닫기
cancelBtn.addEvnetListener('click', function(){
	modal.style.display = 'none';
});

// 삭제 버튼 클릭시 실행(여기서 실제 삭제 로직 추가 가능)
confirmBtn.addEventListstener('click', function(){
	alert('삭제되었습니다.');
	modal.style.display = 'none';
});

// 모달 밖 클릭시 모달 닫기
window.onclick = function(event){
	if(event.target == modal){
		modal.style.display = ' none';
	}
};























